package com.nancy.recipedelight.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.nancy.recipedelight.ui.viewmodel.GeminiViewModel

@Composable
fun MainScreen(chatViewModel: GeminiViewModel) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomBar(navController) }
    ) { padding ->
        AppNavHost(
            modifier = Modifier.padding(padding),
            navController  = navController,
            chatViewModel = chatViewModel
        )
    }
}
