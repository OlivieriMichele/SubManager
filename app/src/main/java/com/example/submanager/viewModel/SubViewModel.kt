package com.example.submanager.viewModel

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import com.example.submanager.model.Subscription
import com.example.submanager.model.Category
import com.example.submanager.ui.theme.AccentColors
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.mutableIntStateOf

class SubViewModel : ViewModel() {

    // ===================================================================
    // 1. STATO FISSO E DATI SORGENTE
    // ===================================================================

    private val _isDark = mutableStateOf(true)
    private val _isEditingState = mutableStateOf(false)
    private val _saveTrigger = mutableIntStateOf(0)
    val isDark: State<Boolean> = _isDark
    val isEditingState: State<Boolean> = _isEditingState
    val saveTrigger: State<Int> = _saveTrigger

    fun setEditingMode(editing: Boolean) {
        _isEditingState.value = editing
    }

    fun resetEditingMode(){
        _isEditingState.value = false
    }

    fun triggerSave() {
        _saveTrigger.intValue ++
    }

    fun resetSaveTrigger() {
        _saveTrigger.intValue = 0
    }

    fun toggleDarkMode() {
        _isDark.value = !_isDark.value
    }

    // LISTA DI CATEGORIE: Dati fissi, usati come base per i calcoli
    private val categories = listOf(
        Category(
            name = "Intrattenimento",
            count = 0,
            total = 0.0,
            icon = Icons.Default.Tv,
            lightGradientStart = AccentColors.lightBlue,
            lightGradientEnd = AccentColors.lightPurple,
            darkGradientStart = AccentColors.darkBlue,
            darkGradientEnd = AccentColors.darkPurple
        ),
        Category(
            name = "Software",
            count = 0,
            total = 0.0,
            icon = Icons.Default.Code,
            lightGradientStart = AccentColors.lightPurple,
            lightGradientEnd = AccentColors.lightIndigo,
            darkGradientStart = AccentColors.darkPurple,
            darkGradientEnd = AccentColors.darkIndigo
        ),
        Category(
            name = "Fitness",
            count = 0,
            total = 0.0,
            icon = Icons.Default.FitnessCenter,
            lightGradientStart = AccentColors.lightIndigo,
            lightGradientEnd = AccentColors.lightBlue,
            darkGradientStart = AccentColors.darkIndigo,
            darkGradientEnd = AccentColors.darkBlue
        ),
        Category(
            name = "Shopping",
            count = 0,
            total = 0.0,
            icon = Icons.Default.ShoppingCart,
            lightGradientStart = AccentColors.lightPurple,
            lightGradientEnd = AccentColors.lightPink,
            darkGradientStart = AccentColors.darkPurple,
            darkGradientEnd = AccentColors.darkPink
        )
    )

    // LISTA DI SOTTOSCRIZIONI: Stato reattivo
    private val _subscriptions = mutableStateOf(
        listOf(
            Subscription(1, "Netflix", 12.99, AccentColors.pastelPurple, "20 Ott", "Intrattenimento"),
            Subscription(2, "Spotify", 9.99, AccentColors.pastelBlue, "15 Ott", "Intrattenimento"),
            Subscription(3, "Adobe CC", 24.99, AccentColors.pastelIndigo, "28 Ott", "Software"),
            Subscription(4, "Amazon Prime", 4.99, AccentColors.pastelPink, "05 Nov", "Intrattenimento"),
            Subscription(5, "GitHub Pro", 4.00, AccentColors.pastelYellow, "12 Nov", "Software"),
            Subscription(6, "Planet Fitness", 29.99, AccentColors.pastelGreen, "01 Nov", "Fitness")
        )
    )
    val subscriptions: State<List<Subscription>> = _subscriptions


    // ===================================================================
    // 2. DATI CALCOLATI
    // ===================================================================

    val categoriesState: State<List<Category>>
        get() {
            val groups = subscriptions.value.groupBy { it.category }
            val categoryStats = categories.map { category ->
                val subs = groups[category.name] ?: emptyList()
                val count = subs.size
                val total = subs.sumOf { it.price }
                category.copy(count = count, total = total)
            }
            return mutableStateOf(categoryStats)
        }

    // ===================================================================
    // 3. METODI DI UTILITÀ
    // ===================================================================

    fun getTotalMonthly(): Double = subscriptions.value.sumOf { it.price }

    fun getTotalYearly(): Double = getTotalMonthly() * 12

    fun getCategorySubscriptions(categoryName: String): List<Subscription> {
        return subscriptions.value.filter { it.category == categoryName }
    }

    fun getCategoryDetails(categoryName: String): Category? {
        return categoriesState.value.find { it.name == categoryName }
    }

    // ===================================================================
    // NUOVI METODI PER GESTIONE SOTTOSCRIZIONI
    // ===================================================================

    /**
     * Ottiene una sottoscrizione tramite il suo ID
     */
    fun getSubscriptionById(id: Int): Subscription? {
        return subscriptions.value.find { it.id == id }
    }

    /**
     * Aggiunge una nuova sottoscrizione
     */
    fun addSubscription(subscription: Subscription) {
        _subscriptions.value = _subscriptions.value + subscription
    }

    /**
     * Aggiorna una sottoscrizione esistente
     */
    fun updateSubscription(subscription: Subscription) {
        _subscriptions.value = _subscriptions.value.map {
            if (it.id == subscription.id) subscription else it
        }
    }

    /**
     * Elimina una sottoscrizione tramite ID
     */
    fun deleteSubscription(id: Int) {
        _subscriptions.value = _subscriptions.value.filter { it.id != id }
    }
}
