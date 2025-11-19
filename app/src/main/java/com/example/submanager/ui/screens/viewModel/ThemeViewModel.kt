package com.example.submanager.ui.screens.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.submanager.data.models.Theme
import com.example.submanager.data.repositories.ThemeRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ThemeState(val theme: Theme)

interface ThemeAction {
    fun toggleAutoTheme(currentTheme: Theme)
    fun changeManualTheme(theme: Theme)
    fun getThemeState(): ThemeState
}

class ThemeViewModel(
    private val themeRepository: ThemeRepository
) : ViewModel() {

    val state = themeRepository.theme
        .map{ ThemeState(it) }
        .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = ThemeState(Theme.System)
    )

    fun changeTheme(theme: Theme) = viewModelScope.launch {
        themeRepository.setTheme(theme)
    }

    val actions = object : ThemeAction {
        override fun getThemeState(): ThemeState{
            val theme = state.value
            return theme
        }

        override fun toggleAutoTheme(currentTheme: Theme) {
            viewModelScope.launch {
                val newTheme = if (currentTheme == Theme.System) {
                    Theme.Light // Passa a manuale con Light come default
                } else {
                    Theme.System // Passa ad automatico
                }
                themeRepository.setTheme(newTheme)
            }
        }

        override fun changeManualTheme(theme: Theme) {
            if (theme != Theme.System) {
                viewModelScope.launch {
                    themeRepository.setTheme(theme)
                }
            }
        }
    }
}