package com.nancy.recipedelight.domain.repositories

import com.nancy.recipedelight.data.remote.GeminiApiService

/** Handles all business logic related to Gemini API.
 * Depends only on the interface, not Android-specific code.
 * Takes an api instance (interface) and calls it. **/

class GeminiRepository(private val api: GeminiApiService) {
    suspend fun generateText(prompt: String): String {
        return api.generateContent(prompt)
    }
}
