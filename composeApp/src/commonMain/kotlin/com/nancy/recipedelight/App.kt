package com.nancy.recipedelight

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.nancy.recipedelight.navigation.MainScreen
import com.nancy.recipedelight.ui.viewmodel.GeminiViewModel

@Composable
fun App(chatViewModel: GeminiViewModel) {
    MaterialTheme {
        MainScreen(chatViewModel = chatViewModel)
    }
}