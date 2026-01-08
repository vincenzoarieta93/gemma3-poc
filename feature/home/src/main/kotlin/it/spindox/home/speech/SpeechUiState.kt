package it.spindox.home.speech

data class SpeechUiState(
    val isListening: Boolean,
    val audioLevel: Float?,
    val recognizedText: String,
    val hasAudioPermission: Boolean,
    val errorMessage: String? = null
)

val emptySpeechUiState = SpeechUiState(
    isListening = false,
    audioLevel = null,
    recognizedText = "",
    hasAudioPermission = false,
    errorMessage = null
)