package it.spindox.data.voicecontroller

import it.spindox.data.model.SpeechEvent
import kotlinx.coroutines.flow.SharedFlow

interface SpeechRecognizerController {

    /** Flusso di eventi dello speech */
    val events: SharedFlow<SpeechEvent>

    /** Avvia il riconoscimento vocale */
    fun startListening()

    /** Ferma il riconoscimento vocale */
    fun stopListening()

    /** Rilascia risorse */
    fun destroy()
}