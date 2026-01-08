package it.spindox.home.speech

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import it.spindox.coroutine.DefaultDispatcherProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SpeechViewModel @Inject constructor(
    private val dispatcherProvider: DefaultDispatcherProvider
) : ViewModel() {

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
        TODO("Not yet implemented")
    }

    fun stopListening() {
        TODO("Not yet implemented")
    }
}