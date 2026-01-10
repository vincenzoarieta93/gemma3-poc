package it.spindox.home.speech

sealed class SpeechUiEvent {
    data class ShowSnackbar(val message: String) : SpeechUiEvent()
    data class NavigateToDestination(val destination: String) : SpeechUiEvent()
}