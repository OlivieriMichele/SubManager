package com.example.submanager.data.repositories

import com.example.submanager.data.local.PreferencesManager
import com.example.submanager.data.models.Theme
import kotlinx.coroutines.flow.Flow

/**
 * Repository per le impostazioni del tema
 */
class ThemeRepository(
    private val preferencesManager: PreferencesManager
) {
    val theme: Flow<Theme> = preferencesManager.theme

    suspend fun setTheme(theme: Theme){
        preferencesManager.setTheme(theme)
    }
}