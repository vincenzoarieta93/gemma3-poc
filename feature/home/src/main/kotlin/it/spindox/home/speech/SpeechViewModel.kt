package it.spindox.home.speech

import android.speech.SpeechRecognizer
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.spindox.coroutine.DefaultDispatcherProvider
import it.spindox.data.model.FunctionCallEvent
import it.spindox.data.model.SpeechEvent
import it.spindox.data.model.ThemeAppearance
import it.spindox.data.repository.abstraction.InferenceModelRepository
import it.spindox.data.voicecontroller.SpeechRecognizerController
import it.spindox.domain.usecase.GetThemeUseCase
import it.spindox.domain.usecase.SendMessageUseCase
import it.spindox.domain.usecase.SetThemeUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
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

    private val currentTheme: MutableStateFlow<ThemeAppearance> = MutableStateFlow(ThemeAppearance.LIGHT)
    private val _speechRouteUiEvent: MutableSharedFlow<SpeechUiEvent> = MutableSharedFlow()
    val speechRouteUiEvent: SharedFlow<SpeechUiEvent> = _speechRouteUiEvent

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
                        _uiState.value.recognizedText.takeUnless { it.isBlank() }?.let {
                            Log.d(TAG, "Sending message: $it")
                            sendMessageUseCase(it)
                        }
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
            sendMessageUseCase.events.collect { event ->
                when (event) {
                    is FunctionCallEvent.SwitchTheme -> {
                        Log.d(TAG, "Invoked Switch theme function!")
                        toggleTheme()
                        _speechRouteUiEvent.emit(SpeechUiEvent.ShowSnackbar("Theme changed successfully"))
                    }

                    is FunctionCallEvent.NavigateToDestination -> {
                        Log.d(TAG, "Navigate to destination ${event.destination}")
                        _speechRouteUiEvent.emit(SpeechUiEvent.NavigateToDestination(event.destination))
                    }

                    is FunctionCallEvent.Error -> {
                        _speechRouteUiEvent.tryEmit(SpeechUiEvent.ShowSnackbar(event.message))
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