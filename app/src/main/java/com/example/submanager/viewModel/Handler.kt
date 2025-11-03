package com.example.submanager.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf

class Handler {
    private val _isDark = mutableStateOf(true)
    val isDark: State<Boolean> = _isDark

    private val _isEditingState = mutableStateOf(false)
    val isEditingState: State<Boolean> = _isEditingState

    private val _saveTrigger = mutableIntStateOf(0)
    val saveTrigger: State<Int> = _saveTrigger

    private val _saveCategoryTrigger = mutableIntStateOf(0)
    val saveCategoryTrigger: State<Int> = _saveCategoryTrigger

    fun toggleDarkMode() {
        _isDark.value = !_isDark.value
    }

    fun setEditingMode(editing: Boolean) {
        _isEditingState.value = editing
    }

    fun resetEditingMode() {
        _isEditingState.value = false
    }

    fun triggerSave() {
        _saveTrigger.intValue++
    }

    fun resetSaveTrigger() {
        _saveTrigger.intValue = 0
    }

    fun triggerSaveCategory() {
        _saveCategoryTrigger.intValue++
    }

    fun resetSaveCategoryTrigger() {
        _saveCategoryTrigger.intValue = 0
    }
}