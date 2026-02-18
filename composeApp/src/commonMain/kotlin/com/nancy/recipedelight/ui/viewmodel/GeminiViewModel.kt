package com.nancy.recipedelight.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nancy.recipedelight.domain.repositories.GeminiRepository
import com.nancy.recipedelight.ui.chefai.ChatMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/* Handles UI state for Gemini content generation.
 * Keeps the UI state (result) in a StateFlow.
 * Calls repository.generateText when the user wants content.  */
class GeminiViewModel(private val repository: GeminiRepository) : ViewModel() {

    private val _chatHistory = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatHistory: StateFlow<List<ChatMessage>> = _chatHistory

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun sendMessage(userInput: String) {
        if (userInput.isBlank()) return

        val userMessage = ChatMessage(role = "user", text = userInput)
        _chatHistory.value = _chatHistory.value + userMessage

        _isLoading.value = true // Start thinking...

        viewModelScope.launch {
            try {
                val response = repository.generateText(_chatHistory.value)
                val modelMessage = ChatMessage(role = "model", text = response)
                _chatHistory.value = _chatHistory.value + modelMessage
            } catch (e: Exception) {
                _chatHistory.value = _chatHistory.value + ChatMessage(role = "model", text = "Error: ${e.localizedMessage}")
            } finally {
                _isLoading.value = false // Stop thinking
            }
        }
    }
}