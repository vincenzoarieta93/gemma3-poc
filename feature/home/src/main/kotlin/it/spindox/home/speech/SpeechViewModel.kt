package it.spindox.home.speech

import android.speech.SpeechRecognizer
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.spindox.coroutine.DefaultDispatcherProvider
import it.spindox.data.model.FunctionCallEvent
import it.spindox.data.model.LlmResponse
import it.spindox.data.model.SpeechEvent
import it.spindox.data.model.ThemeAppearance
import it.spindox.data.repository.abstraction.InferenceModelRepository
import it.spindox.data.voicecontroller.SpeechRecognizerController
import it.spindox.domain.usecase.GetThemeUseCase
import it.spindox.domain.usecase.SendMessageUseCase
import it.spindox.domain.usecase.SetThemeUseCase
import it.spindox.result.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SpeechViewModel @Inject constructor(
    private val dispatcherProvider: DefaultDispatcherProvider,
    private val speechRecognizerController: SpeechRecognizerController,
    private val inferenceModelRepository: InferenceModelRepository,
    private val sendMessageUseCase: SendMessageUseCase,
    private val getThemeUseCase: GetThemeUseCase,
    private val setThemeUseCase: SetThemeUseCase,
) : ViewModel() {

    companion object {
        private const val TAG: String = "SpeechViewModel"
    }

    private val currentTheme: MutableStateFlow<ThemeAppearance> =
        MutableStateFlow(ThemeAppearance.LIGHT)
    private val _speechRouteUiEvent: MutableSharedFlow<SpeechUiEvent> = MutableSharedFlow()
    val speechRouteUiEvent: SharedFlow<SpeechUiEvent> = _speechRouteUiEvent

    private val promptFlow = MutableSharedFlow<String>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    init {
        getTheme()
        viewModelScope.launch {
            speechRecognizerController.events.collect { event ->
                when (event) {
                    is SpeechEvent.Error -> {
                        Log.d(TAG, "Got error: ${event.errorCode}")
                        _uiState.update { oldState ->
                            oldState.copy(
                                audioLevel = 0f,
                                isListening = false,
                                status = if (event.errorCode == SpeechRecognizer.ERROR_CLIENT) {
                                    oldState.status
                                } else {
                                    SpeechStatus.ERROR
                                },
                                recognizedText = if (event.errorCode == SpeechRecognizer.ERROR_CLIENT) {
                                    ""
                                } else {
                                    oldState.recognizedText
                                },
                                errorMessage = if (event.errorCode == SpeechRecognizer.ERROR_CLIENT) {
                                    null
                                } else {
                                    "An error occurred"
                                }

                            )
                        }
                    }

                    is SpeechEvent.Final -> {
                        Log.d(TAG, "Final: ${event.text}")
                        _uiState.update { oldState ->
                            oldState.copy(
                                isListening = false,
                                recognizedText = event.text
                            )
                        }

                        // Send message to the model
                        event.text
                            .takeUnless { it.isBlank() }
                            ?.let { promptFlow.tryEmit(it) }
                    }

                    is SpeechEvent.Partial -> {
                        Log.d(TAG, "Partial: ${event.text}")
                        _uiState.update { oldState ->
                            oldState.copy(
                                recognizedText = event.text
                            )
                        }
                    }

                    SpeechEvent.Ready -> {
                        Log.d(TAG, "Ready!")
                        _uiState.update { oldState ->
                            oldState.copy(
                                isListening = true,
                                recognizedText = "",
                                status = SpeechStatus.SUCCESS
                            )
                        }
                    }

                    is SpeechEvent.Rms -> {
                        onRmsChanged(event.rms)
                    }
                }
            }
        }

        viewModelScope.launch {
            promptFlow
                .flatMapLatest { prompt ->
                    sendMessageUseCase(prompt)
                }
                .collectLatest { response ->
                    when (response) {
                        is Resource.Error -> {
                            FunctionCallEvent.Error(response.getErrorMessage())
                        }

                        is Resource.Success -> {
                            when (val llmResponse = response.data) {
                                is LlmResponse.Text -> {
                                    llmResponse.text
                                }

                                is LlmResponse.SwitchThemeCall -> {
                                    toggleTheme()
                                    _speechRouteUiEvent.emit(SpeechUiEvent.ShowSnackbar("Theme changed successfully"))
                                }

                                is LlmResponse.NavigateToDestination -> {
                                    Log.d(TAG, "Navigate to destination ${llmResponse.destination}")
                                    _speechRouteUiEvent.emit(
                                        SpeechUiEvent.NavigateToDestination(
                                            llmResponse.destination
                                        )
                                    )
                                }

                                is LlmResponse.UnknownFunctionCall -> {
                                    FunctionCallEvent.Error(llmResponse.message)
                                }
                            }
                        }

                        else -> {
                            // TODO("Update _uiState")
                        }
                    }
                }
        }
    }

    private val _uiState: MutableStateFlow<SpeechUiState> =
        MutableStateFlow(emptySpeechUiState)
    val uiState: StateFlow<SpeechUiState> = _uiState

    fun onRmsChanged(rms: Float) {
        _uiState.update { oldState ->
            oldState.copy(
                audioLevel = rms.coerceIn(0f, 10f)

            )
        }
    }

    fun onAudioPermissionUpdated(hasPermission: Boolean) {
        _uiState.update { oldState ->
            oldState.copy(
                hasAudioPermission = hasPermission
            )
        }
    }

    fun startListening() {
        speechRecognizerController.startListening()
    }

    fun stopListening() {
        speechRecognizerController.stopListening()
    }

    suspend fun initializeAsync() = withContext(Dispatchers.IO) {
        _uiState.update { oldState ->
            oldState.copy(
                status = SpeechStatus.LOADING
            )
        }

        inferenceModelRepository.startChat()

        _uiState.update { oldState ->
            oldState.copy(
                status = SpeechStatus.SUCCESS
            )
        }
    }

    private fun getTheme() {
        viewModelScope.launch {
            getThemeUseCase().collectLatest { theme ->
                currentTheme.value = theme
            }
        }
    }

    private fun toggleTheme() {
        viewModelScope.launch {
            setThemeUseCase(
                if (currentTheme.value == ThemeAppearance.DARK) {
                    ThemeAppearance.LIGHT
                } else {
                    ThemeAppearance.DARK
                }
            )
        }
    }
}