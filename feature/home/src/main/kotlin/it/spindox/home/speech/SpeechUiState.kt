package it.spindox.home.speech

data class SpeechUiState(
    val status: SpeechStatus,
    val isListening: Boolean,
    val audioLevel: Float?,
    val recognizedText: String,
    val hasAudioPermission: Boolean,
    val errorMessage: String? = null
)

enum class SpeechStatus {
    LOADING,
    SUCCESS,
    ERROR
}

val emptySpeechUiState = SpeechUiState(
    status = SpeechStatus.LOADING,
    isListening = false,
    audioLevel = null,
    recognizedText = "",
    hasAudioPermission = false,
    errorMessage = null
)