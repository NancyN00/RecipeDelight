package com.nancy.recipedelight.domain.repositories

import com.nancy.recipedelight.data.remote.GeminiApiService
import com.nancy.recipedelight.ui.chefai.ChatMessage
import com.nancy.recipedelight.data.local.ChatEntityQueries


/** Handles all business logic related to Gemini API.
 * Depends only on the interface, not Android-specific code.
 * Takes an api instance (interface) and calls it. **/

class GeminiRepository(
    private val api: GeminiApiService,
    private val queries: ChatEntityQueries
) {
    // 1. Fetch History from SQLDelight
    fun getLocalHistory(mealId: String): List<ChatMessage> {
        return queries.getMessagesByMealId(mealId).executeAsList().map {
            ChatMessage(role = it.role, text = it.message)
        }
    }

    // 2. Process AI logic and save to DB
    suspend fun generateText(
        mealId: String,
        history: List<ChatMessage>,
        firstMsgContext: String
    ): String {
        // Save User Message to local DB
        val lastUserMsg = history.last()
        queries.insertMessage(mealId, "user", lastUserMsg.text, System.currentTimeMillis())

        // Prepare context-aware history for the Gemini API call
        val apiHistory = history.toMutableList().apply {
            if (this.isNotEmpty()) {
                this[0] = ChatMessage(role = "user", text = firstMsgContext)
            }
        }

        // Call Gemini API
        val response = api.generateContent(apiHistory)

        // Save AI Response to local DB
        queries.insertMessage(mealId, "model", response, System.currentTimeMillis())

        return response
    }

    fun clearLocalChat(mealId: String) {
        queries.clearChatByMealId(mealId)
    }
}