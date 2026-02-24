package com.nancy.recipedelight

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.nancy.recipedelight.ui.theme.RecipeDelightTheme
import com.nancy.recipedelight.ui.viewmodel.GeminiViewModel
import com.nancy.recipedelight.ui.viewmodel.SettingsViewModel
import com.nancy.recipedelight.voice.VoiceRecognizer
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    // Koin injects everything automatically: ApiService -> Queries -> Repo -> ViewModel
    // private val geminiViewModel: GeminiViewModel by viewModel()
    private val settingsViewModel: SettingsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        installSplashScreen()
        super.onCreate(savedInstanceState)

        //  Request Audio Permission on Startup (or when needed)
        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (!isGranted) {
                // Handle permission denied (e.g., show a Toast)
                Toast.makeText(this, "Microphone permission is required for Voice AI", Toast.LENGTH_SHORT).show()
            }
        }

        // Check if already have it, if not, ask!
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }


        Log.d("GEMINI_KEY", BuildConfig.GEMINI_API_KEY)

        setContent {

            val isDarkTheme by settingsViewModel.isDarkMode.collectAsState()
            //     val voiceRecognizer = remember { VoiceRecognizer(context) }

            RecipeDelightTheme(darkTheme = isDarkTheme) {
                // Pass voiceRecognizer here
                App(
                    //  chatViewModel = geminiViewModel,
                    //   voiceRecognizer = voiceRecognizer
                )
            }

        }
    }
}
