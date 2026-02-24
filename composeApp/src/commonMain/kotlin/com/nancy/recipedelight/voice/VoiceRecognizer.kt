package com.nancy.recipedelight.voice

interface VoiceRecognizer {
    fun startListening(onResult: (String) -> Unit)
    fun stopListening()
}
