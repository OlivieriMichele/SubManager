package com.example.submanager.ui.screens.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.submanager.data.models.Theme
import com.example.submanager.data.repositories.ThemeRepository
import kotlinx.coroutines.launch

/**
 * ProfileViewModel - Gestisce la logica della schermata Profilo
 *
 * Coordina le azioni tra AuthViewModel e ThemeViewModel
 * per evitare duplicazione di stato
 */
class ProfileViewModel(
    private val themeRepository: ThemeRepository
) : ViewModel() {

    /**
     * Toggle tra Tema Automatico (System) e Tema Manuale
     * Se è System -> passa a Light
     * Se è Light/Dark -> passa a System
     */
    fun toggleAutoTheme(currentTheme: Theme) {
        viewModelScope.launch {
            val newTheme = if (currentTheme == Theme.System) {
                Theme.Light // Passa a manuale con Light come default
            } else {
                Theme.System // Passa ad automatico
            }
            themeRepository.setTheme(newTheme)
        }
    }

    /**
     * Cambia il tema manuale tra Light e Dark
     * (solo se non è in modalità System)
     */
    fun changeManualTheme(theme: Theme) {
        if (theme != Theme.System) {
            viewModelScope.launch {
                themeRepository.setTheme(theme)
            }
        }
    }
}