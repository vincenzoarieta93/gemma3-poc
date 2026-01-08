package it.spindox.data.voicecontroller

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import it.spindox.data.model.SpeechEvent
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject

class SpeechRecognizerControllerImpl @Inject constructor(
    private val context: Context
    ) : SpeechRecognizerController {

        override val events = MutableSharedFlow<SpeechEvent>(extraBufferCapacity = 10)

        private val recognizer = SpeechRecognizer.createSpeechRecognizer(context)

        private val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }

        init {
            recognizer.setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle) {
                    events.tryEmit(SpeechEvent.Ready)
                }

                override fun onRmsChanged(rmsdB: Float) {
                    events.tryEmit(SpeechEvent.Rms(rmsdB))
                }

                override fun onBufferReceived(buffer: ByteArray?) {
                    // Do nothing for now
                }

                override fun onPartialResults(partialResults: Bundle) {
                    val text = partialResults
                        .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        ?.firstOrNull()
                        .orEmpty()
                    events.tryEmit(SpeechEvent.Partial(text))
                }

                override fun onResults(results: Bundle) {
                    val text = results
                        .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        ?.firstOrNull()
                        .orEmpty()
                    events.tryEmit(SpeechEvent.Final(text))
                }

                override fun onError(error: Int) {
                    events.tryEmit(SpeechEvent.Error)
                }

                override fun onBeginningOfSpeech() {}
                override fun onEndOfSpeech() {}
                override fun onEvent(eventType: Int, params: Bundle) {}
            })
        }

        override fun startListening() {
            recognizer.startListening(intent)
        }

        override fun stopListening() {
            recognizer.stopListening()
        }

        override fun destroy() {
            recognizer.destroy()
        }
    }
