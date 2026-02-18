package com.nancy.recipedelight.data.remote

import com.nancy.recipedelight.ui.chefai.ChatMessage

// Defines the contract for generating content. making API calls
// No platform-specific code here, can be used anywhere.
interface GeminiApiService {
    suspend fun generateContent(history: List<ChatMessage>): String
}
