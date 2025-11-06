package com.example.submanager.data.repositories

import com.example.submanager.data.local.PreferencesManager
import com.example.submanager.model.Theme
import kotlinx.coroutines.flow.Flow

/**
 * Repository per le impostazioni del tema
 */
class ThemeRepository(
    private val preferencesManager: PreferencesManager
) {
    val isDarkMode: Flow<Boolean> = preferencesManager.isDarkMode

    suspend fun setDarkMode(enabled: Boolean) {
        preferencesManager.setDarkMode(enabled)
    }

    suspend fun toggleDarkMode() {
        preferencesManager.toggleDarkMode()
    }
}