package it.spindox.home.speech

data class SpeechUiState(
    val isListening: Boolean,
    val audioLevel: Float?,
    val recognizedText: String,
    val hasAudioPermission: Boolean
)

val emptySpeechUiState = SpeechUiState(
    isListening = false,
    audioLevel = null,
    recognizedText = "",
    hasAudioPermission = false
)