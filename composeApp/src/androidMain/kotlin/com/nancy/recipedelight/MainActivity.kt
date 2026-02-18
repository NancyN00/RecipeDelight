package com.nancy.recipedelight

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.nancy.recipedelight.domain.repositories.GeminiRepository
import com.nancy.recipedelight.repo.GeminiApiServiceImpl
import com.nancy.recipedelight.ui.viewmodel.GeminiViewModel

// MainActivity.kt
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        installSplashScreen()
        super.onCreate(savedInstanceState)

        // Initialize Gemini API and ViewModel
        val apiService = GeminiApiServiceImpl(BuildConfig.GEMINI_API_KEY) // pass API key
        val repository = GeminiRepository(apiService)
        val viewModel = GeminiViewModel(repository)
        Log.d("GEMINI_KEY", BuildConfig.GEMINI_API_KEY)


        setContent {
            App(chatViewModel = viewModel)
        }
    }
}
