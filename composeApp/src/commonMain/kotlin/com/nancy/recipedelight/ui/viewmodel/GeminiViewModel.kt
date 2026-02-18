package com.nancy.recipedelight.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nancy.recipedelight.BuildConfig
import com.nancy.recipedelight.domain.repositories.GeminiRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/* Handles UI state for Gemini content generation.
 * Keeps the UI state (result) in a StateFlow.
 * Calls repository.generateText when the user wants content.  */
class GeminiViewModel(private val repository: GeminiRepository) : ViewModel() {

    private val _result = MutableStateFlow("")
    val result: StateFlow<String> = _result

    fun generate(prompt: String) {
        // viewModelScope to handle lifecycle automatically
        viewModelScope.launch {
            try {
                _result.value = "Thinking..."
                val response = repository.generateText(prompt)
                _result.value = response
            } catch (e: Exception) {
                _result.value = "Error: ${e.localizedMessage}"
            }
        }
    }
}