package com.nancy.recipedelight.data.remote

// Defines the contract for generating content. making API calls
// No platform-specific code here, can be used anywhere.
interface GeminiApiService {
    suspend fun generateContent(prompt: String): String
}
