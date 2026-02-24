package com.nancy.recipedelight

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.nancy.recipedelight.navigation.MainScreen
import com.nancy.recipedelight.ui.viewmodel.GeminiViewModel
import com.nancy.recipedelight.voice.VoiceRecognizer

@Composable
fun App(
  //  chatViewModel: GeminiViewModel,
   // voiceRecognizer: VoiceRecognizer
) {
    MaterialTheme {
        MainScreen(
          //  chatViewModel = chatViewModel,
          //  voiceRecognizer = voiceRecognizer
        )
    }
}