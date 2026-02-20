package com.nancy.recipedelight.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nancy.recipedelight.domain.repositories.AppSettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: AppSettingsRepository
) : ViewModel() {

    init {
        // THIS avoid blocking main thread and crashing on DB write
        viewModelScope.launch {
            repository.initDefaults()
        }
    }

    val isDarkMode: StateFlow<Boolean> = repository.isDarkMode
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    fun onToggleDarkMode() {
        viewModelScope.launch {
            repository.toggleDarkMode()
        }
    }
}