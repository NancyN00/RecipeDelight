package com.nancy.recipedelight.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.nancy.recipedelight.data.local.AppSettingsQueries
import com.nancy.recipedelight.domain.repositories.AppSettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AppSettingsRepositoryImpl(
    private val queries: AppSettingsQueries
) : AppSettingsRepository {

    override val isDarkMode: Flow<Boolean> =
        queries.getDarkMode()
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map { isDark -> (isDark ?: 0L) != 0L }

    override fun toggleDarkMode() {
        val current = queries.getDarkMode().executeAsOneOrNull() ?: 0L
        queries.updateDarkMode(if (current == 0L) 1L else 0L)
    }

    override fun initDefaults() {
        queries.insertDefaultSettings()
    }
}
