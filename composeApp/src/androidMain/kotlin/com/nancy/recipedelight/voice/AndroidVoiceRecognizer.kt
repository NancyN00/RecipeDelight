package com.nancy.recipedelight.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer

class AndroidVoiceRecognizer(private val context: Context) : VoiceRecognizer {
    private var speechRecognizer: SpeechRecognizer? = null
    private val mainHandler = Handler(Looper.getMainLooper())

    override fun startListening(onResult: (String) -> Unit) {
        mainHandler.post {
            // Safety check
            if (!SpeechRecognizer.isRecognitionAvailable(context)) {
                onResult("Error: Speech recognition not available")
                return@post
            }

            if (speechRecognizer != null) {
                speechRecognizer?.stopListening()
                speechRecognizer?.cancel()
            } else {
                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
            }

            // Re-attach the listener every time to ensure context is fresh
            speechRecognizer?.setRecognitionListener(object : RecognitionListener {
                override fun onResults(results: Bundle?) {
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    val text = matches?.firstOrNull() ?: "No speech detected"
                    onResult(text)
                }

                override fun onError(error: Int) {
                    val errorMessage = when (error) {
                        SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Permission Denied"
                        SpeechRecognizer.ERROR_NETWORK -> "Network Error"
                        SpeechRecognizer.ERROR_NO_MATCH -> "No speech recognized"
                        SpeechRecognizer.ERROR_CLIENT -> "Client Error (Error 5): Try again or check Google App settings"
                        SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "System is busy, please wait..."
                        else -> "Error code: $error"
                    }
                    onResult(errorMessage)
                }

                override fun onReadyForSpeech(params: Bundle?) {}
                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() {}
                override fun onPartialResults(partialResults: Bundle?) {}
                override fun onEvent(eventType: Int, params: Bundle?) {}
            })

            // Build the Intent with calling package (required for many devices)
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            }

            speechRecognizer?.startListening(intent)
        }
    }

    override fun stopListening() {
        mainHandler.post {
            speechRecognizer?.stopListening()
            speechRecognizer?.cancel()
        }
    }

    // Call this from ViewModel onCleared to release memory
    fun destroy() {
        mainHandler.post {
            speechRecognizer?.destroy()
            speechRecognizer = null
        }
    }
}
