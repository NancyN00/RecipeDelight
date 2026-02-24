package com.nancy.recipedelight.voice

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.annotation.RequiresApi
import com.nancy.recipedelight.voice.VoiceRecognizer

class AndroidVoiceRecognizer(private val context: Context) : VoiceRecognizer {
    private var speechRecognizer: SpeechRecognizer? = null
    private val mainHandler = Handler(Looper.getMainLooper())

    override fun startListening(onResult: (String) -> Unit) {
        mainHandler.post {
            // Safety check for availability
            if (!SpeechRecognizer.isRecognitionAvailable(context)) {
                onResult("Error: Speech recognition not available on this device")
                return@post
            }

            // Clean up any existing instance
            stopListening()

            // Initialize Recognizer
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(object : RecognitionListener {
                    override fun onResults(results: Bundle?) {
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        onResult(matches?.firstOrNull() ?: "No speech detected")
                    }

                    override fun onError(error: Int) {
                        val errorMessage = when (error) {
                            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Permission Denied"
                            SpeechRecognizer.ERROR_NETWORK -> "Network Error"
                            else -> "Error code: $error"
                        }
                        onResult(errorMessage)
                    }

                    // Empty overrides required by interface
                    override fun onReadyForSpeech(params: Bundle?) {}
                    override fun onBeginningOfSpeech() {}
                    override fun onRmsChanged(rmsdB: Float) {}
                    override fun onBufferReceived(buffer: ByteArray?) {}
                    override fun onEndOfSpeech() {}
                    override fun onPartialResults(partialResults: Bundle?) {}
                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })
            }

            // 4. Build the Intent
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            }

            speechRecognizer?.startListening(intent)
        }
    }

    override fun stopListening() {
        mainHandler.post {
            speechRecognizer?.stopListening()
            speechRecognizer?.cancel() // cancels the current task
            speechRecognizer?.destroy()
            speechRecognizer = null
        }
    }
}
