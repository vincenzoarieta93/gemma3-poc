package it.spindox.home.speech

import android.speech.SpeechRecognizer
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.spindox.coroutine.DefaultDispatcherProvider
import it.spindox.data.model.LlmResponse
import it.spindox.data.model.SpeechEvent
import it.spindox.data.model.ThemeAppearance
import it.spindox.data.repository.abstraction.InferenceModelRepository
import it.spindox.data.utils.EdgeFunctionUtils
import it.spindox.data.voicecontroller.SpeechRecognizerController
import it.spindox.domain.usecase.GetThemeUseCase
import it.spindox.domain.usecase.SendMessageUseCase
import it.spindox.domain.usecase.SetThemeUseCase
import it.spindox.navigation.AppRoute
import it.spindox.result.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
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
                                status = oldState.status,
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
                                promptState = PromptState.PROCESSING,
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
                                promptState = PromptState.IDLE,
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
                            handleInferenceError(response)
                        }

                        is Resource.Success -> {
                            handleInferenceSuccess(response)
                        }

                        else -> {
                            _uiState.update { oldState ->
                                oldState.copy(
                                    promptState = PromptState.PROCESSING
                                )
                            }
                        }
                    }
                }
        }
    }

    private suspend fun handleInferenceSuccess(response: Resource.Success<LlmResponse>) {
        var promptState = PromptState.SUCCESS
        var errorMessage = ""

        when (val llmResponse = response.data) {
            is LlmResponse.Text -> {
                // No function detected by the model. Do nothing
                Log.d(TAG, "Got text: ${llmResponse.text}")
                promptState = PromptState.ERROR
                errorMessage =
                    "Model has not detected any function call. It has replied with simple text"
            }

            is LlmResponse.SwitchThemeCall -> {
                // SwitchThemeCall detected. Toggle theme
                Log.d(TAG, "${EdgeFunctionUtils.SWITCH_THEME_FUN_DECLARATION} detected")
                toggleTheme()
                _speechRouteUiEvent.emit(SpeechUiEvent.ShowSnackbar("Theme changed successfully"))
            }

            is LlmResponse.NavigateToDestination -> {
                // NavigateToDestination detected. Navigate to destination
                Log.d(TAG, "${EdgeFunctionUtils.NAVIGATE_TO_DESTINATION_FUN_DECLARATION} detected. Destination is ${llmResponse.destination}")

                if (llmResponse.destination.contains("home", true)) {
                    _speechRouteUiEvent.emit(
                        SpeechUiEvent.NavigateToDestination(AppRoute.ModelSelectionScreen.route)
                    )
                } else {
                    promptState = PromptState.ERROR
                    errorMessage = "No destination detected for navigation"
                }
            }

            is LlmResponse.OpenWiFiSettingsScreen -> {
                // OpenWiFiSettingsScreen detected. Open WiFi settings
                Log.d(TAG, "${EdgeFunctionUtils.OPEN_SYSTEM_SETTINGS_FUN_DECLARATION} detected")
                _speechRouteUiEvent.emit(
                    SpeechUiEvent.OpenWiFiSettingsScreen
                )
            }

            is LlmResponse.UnknownFunctionCall -> {
                // Unknown function detected
                Log.d(TAG, "Unknown function detected: ${llmResponse.message}")
                promptState = PromptState.ERROR
                errorMessage = "Unknown function detected"
            }
        }

        _uiState.update { oldState ->
            oldState.copy(
                promptState = promptState
            )
        }

        if (promptState == PromptState.ERROR && errorMessage.isNotBlank()) {
            _speechRouteUiEvent.emit(SpeechUiEvent.ShowSnackbar(errorMessage))
        }
    }

    private suspend fun handleInferenceError(response: Resource.Error) {
        _uiState.update { oldState ->
            oldState.copy(
                promptState = PromptState.ERROR
            )
        }
        _speechRouteUiEvent.emit(SpeechUiEvent.ShowSnackbar("An error occurred: ${response.throwable.localizedMessage.orEmpty()}"))
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