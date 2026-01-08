package it.spindox.home.speech

data class SpeechScreenEvent(
    val onStartListening: () -> Unit = {},
    val onStopListening: () -> Unit = {},
    val onRequestPermission: () -> Unit = {}
)