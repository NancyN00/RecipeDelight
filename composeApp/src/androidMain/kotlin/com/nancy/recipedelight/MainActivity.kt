package com.nancy.recipedelight

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.nancy.recipedelight.ui.viewmodel.GeminiViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    // Koin injects everything automatically: ApiService -> Queries -> Repo -> ViewModel
    private val geminiViewModel: GeminiViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        installSplashScreen()
        super.onCreate(savedInstanceState)

        Log.d("GEMINI_KEY", BuildConfig.GEMINI_API_KEY)

        setContent {
            App(chatViewModel = geminiViewModel)
        }
    }
}