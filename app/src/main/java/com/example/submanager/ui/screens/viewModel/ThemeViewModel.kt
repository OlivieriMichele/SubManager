package com.example.submanager.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.submanager.data.repositories.ThemeRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel leggero dedicato SOLO alla gestione del tema
 */
class ThemeViewModel(
    private val themeRepository: ThemeRepository
) : ViewModel() {

    // Espone il tema come StateFlow per la UI
    val isDarkMode: StateFlow<Boolean> = themeRepository.isDarkMode.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = true
    )

    /**
     * Toggle del tema - gestisce la coroutine internamente
     * Può essere chiamato da qualsiasi parte della UI senza problemi
     */
    fun toggleDarkMode() = viewModelScope.launch {
            themeRepository.toggleDarkMode()
        }

    /**
     * Set esplicito del tema (opzionale, ma utile)
     */
    fun setDarkMode(enabled: Boolean) = viewModelScope.launch {
            themeRepository.setDarkMode(enabled)
        }
}