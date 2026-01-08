package it.spindox.data.model

sealed class SpeechEvent {
    object Ready : SpeechEvent()
    data class Partial(val text: String) : SpeechEvent()
    data class Final(val text: String) : SpeechEvent()
    data class Rms(val rms: Float) : SpeechEvent()
    data class Error(val errorCode: Int) : SpeechEvent()
}
