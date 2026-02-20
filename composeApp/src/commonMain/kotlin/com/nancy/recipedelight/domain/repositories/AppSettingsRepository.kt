package com.nancy.recipedelight.domain.repositories

import kotlinx.coroutines.flow.Flow

interface AppSettingsRepository {
    val isDarkMode: Flow<Boolean>
    fun toggleDarkMode()
    fun initDefaults()
}