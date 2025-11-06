package com.example.submanager.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class PreferencesManager(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
    }

    val isDarkMode: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[DARK_MODE_KEY] ?: true // default: dark mode attivo
    }

    suspend fun setDarkMode(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[DARK_MODE_KEY] = enabled
        }
    }

    /**
     * Toggle del tema scuro (pratico per i bottoni)
     */
    suspend fun toggleDarkMode() {
        val currentValue = isDarkMode.first()
        setDarkMode(!currentValue)
    }

    /*
    Todo: update toggle with model.Theme to use system settings like teacher ex
    companion object {
        private val THEME_KEY = stringPreferencesKey("theme")
    }

    val theme = dataStore.data
        .map { preferences ->
            try {
                Theme.valueOf(preferences[THEME_KEY] ?: "System")
            } catch (e: Exception) {
                Theme.System
            }
        }

    suspend fun setTheme(theme: Theme) =
        dataStore.edit { it[THEME_KEY] = theme.toString() }
    */
}

/**
 * NOTE IMPORTANTI:
 *
 * 1. DataStore è ASYNC - tutte le operazioni devono essere chiamate da coroutine
 * 2. Usa Flow per osservare i cambiamenti (reattivo)
 * 3. Per leggere un valore singolo usa: flow.first()
 * 4. Per osservare continuamente usa: flow.collect { value -> ... }
 * 5. Nel ViewModel usa collectAsStateWithLifecycle() per convertire Flow in State
 */
