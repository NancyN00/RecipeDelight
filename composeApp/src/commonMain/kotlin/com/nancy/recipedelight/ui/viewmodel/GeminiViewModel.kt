package com.nancy.recipedelight.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nancy.recipedelight.domain.repositories.GeminiRepository
import com.nancy.recipedelight.ui.chefai.ChatMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GeminiViewModel(private val repository: GeminiRepository) : ViewModel() {

    private val _chatHistory = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatHistory: StateFlow<List<ChatMessage>> = _chatHistory

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Load history from SQLDelight
    fun loadChatHistory(mealId: String) {
        viewModelScope.launch {
            _chatHistory.value = repository.getLocalHistory(mealId)
        }
    }

    fun sendMessage(userInput: String, mealId: String, mealContext: String? = null) {
        if (userInput.isBlank()) return

        val finalInputForAI = if (_chatHistory.value.isEmpty() && mealContext != null) {
            "Context: I am looking at a recipe for $mealContext. User question: $userInput"
        } else {
            userInput
        }

        val userDisplayMessage = ChatMessage(role = "user", text = userInput)
        _chatHistory.value += userDisplayMessage
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val response = repository.generateText(
                    mealId = mealId,
                    history = _chatHistory.value,
                    firstMsgContext = finalInputForAI
                )
                _chatHistory.value += ChatMessage(
                                    role = "model",
                                    text = response
                                )
            } catch (e: Exception) {
                _chatHistory.value += ChatMessage(
                                    role = "model",
                                    text = "Error: ${e.localizedMessage}"
                                )
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearChat(mealId: String) {
        viewModelScope.launch {
            repository.clearLocalChat(mealId)
            _chatHistory.value = emptyList()
        }
    }
}