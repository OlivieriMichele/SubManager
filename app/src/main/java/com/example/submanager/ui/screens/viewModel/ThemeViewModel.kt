package com.example.submanager.ui.screens.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.submanager.data.models.Theme
import com.example.submanager.data.repositories.ThemeRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ThemeState(
    val theme: Theme,           // Tema attuale salvato
    val isAutoTheme: Boolean,   // true se System
    val manualTheme: Theme      // Ultimo tema manuale (Light/Dark)
)

interface ThemeAction {
    fun toggleAutoTheme()
    fun changeManualTheme(newTheme: Theme)
}

class ThemeViewModel(
    private val themeRepository: ThemeRepository
) : ViewModel() {

    private var lastManualTheme: Theme = Theme.Dark

    val state = themeRepository.theme
        .map{ currentTheme ->
            val isAuto = currentTheme == Theme.System
            if(!isAuto) lastManualTheme = currentTheme
            ThemeState(currentTheme, isAuto,
                if(isAuto) lastManualTheme else currentTheme
            )
        }
        .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = ThemeState(Theme.System, isAutoTheme = true, manualTheme = Theme.Dark)
    )

    fun changeTheme(theme: Theme) = viewModelScope.launch {
        themeRepository.setTheme(theme)
    }

    val actions = object : ThemeAction {

        override fun toggleAutoTheme() {
            viewModelScope.launch {
                val currentState = state.value
                if (currentState.isAutoTheme) {
                    changeTheme(lastManualTheme)
                } else {
                    changeTheme(Theme.System)
                }
            }
        }

        override fun changeManualTheme(newTheme: Theme) {
            viewModelScope.launch {
                if (newTheme != Theme.System) {
                    lastManualTheme = newTheme
                    changeTheme(newTheme)
                }
            }
        }
    }
}