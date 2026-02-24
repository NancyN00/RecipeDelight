package com.nancy.recipedelight.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nancy.recipedelight.domain.repositories.GeminiRepository
import com.nancy.recipedelight.ui.chefai.ChatMessage
import com.nancy.recipedelight.voice.VoiceRecognizer
import com.nancy.recipedelight.voice.VoiceState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GeminiViewModel(
    private val repository: GeminiRepository,
    private val voiceRecognizer: VoiceRecognizer
) : ViewModel() {

    private val _chatHistory = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatHistory: StateFlow<List<ChatMessage>> = _chatHistory

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _voiceState = MutableStateFlow(VoiceState())
    val voiceState = _voiceState.asStateFlow()

    private val _userInput = MutableStateFlow("")
    val userInput: StateFlow<String> = _userInput.asStateFlow()

    fun updateUserInput(newText: String) {
        _userInput.value = newText
    }

    fun toggleListening() {
        if (_voiceState.value.isListening) {
            stopVoiceLogic()
        } else {
            startVoiceLogic()
        }
    }

    private fun startVoiceLogic() {
        _voiceState.value = _voiceState.value.copy(isListening = true)

        voiceRecognizer.startListening { recognizedText ->
            _voiceState.value = _voiceState.value.copy(
                text = recognizedText,
                isListening = false
            )
            updateUserInput(recognizedText)
        }
    }

    private fun stopVoiceLogic() {
        voiceRecognizer.stopListening()
        _voiceState.value = _voiceState.value.copy(isListening = false)
    }

    // mic stops if the user leaves the screen/ViewModel is cleared
    override fun onCleared() {
        super.onCleared()
        voiceRecognizer.stopListening()
    }

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

        _chatHistory.value += ChatMessage(role = "user", text = userInput)
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val response = repository.generateText(
                    mealId = mealId,
                    history = _chatHistory.value,
                    firstMsgContext = finalInputForAI
                )
                _chatHistory.value += ChatMessage(role = "model", text = response)
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