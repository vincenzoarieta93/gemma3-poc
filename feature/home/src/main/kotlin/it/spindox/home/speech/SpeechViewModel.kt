package it.spindox.home.speech

import android.speech.SpeechRecognizer
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.spindox.coroutine.DefaultDispatcherProvider
import it.spindox.data.model.SpeechEvent
import it.spindox.data.repository.abstraction.InferenceModelRepository
import it.spindox.data.voicecontroller.SpeechRecognizerController
import it.spindox.result.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SpeechViewModel @Inject constructor(
    private val dispatcherProvider: DefaultDispatcherProvider,
    private val speechRecognizerController: SpeechRecognizerController,
    private val inferenceModelRepository: InferenceModelRepository
) : ViewModel() {

    companion object {
        private const val TAG: String = "SpeechViewModel"
    }

    init {
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
        try {
            _uiState.update { oldState ->
                oldState.copy(
                    status = SpeechStatus.LOADING
                )
            }

            val results = inferenceModelRepository.initialize()
            if (results is Resource.Error) {
                Log.e(TAG, "Error initializing inference model", results.throwable)
                _uiState.update { oldState ->
                    oldState.copy(
                        status = SpeechStatus.ERROR,
                        errorMessage = "Failed to initialize inference model"
                    )
                }
            } else {
                _uiState.update { oldState ->
                    oldState.copy(
                        status = SpeechStatus.SUCCESS
                    )
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, "Error initializing inference model", e)
            _uiState.update { oldState ->
                oldState.copy(
                    status = SpeechStatus.ERROR,
                    errorMessage = "Failed to initialize inference model"
                )
            }
        }
    }
}