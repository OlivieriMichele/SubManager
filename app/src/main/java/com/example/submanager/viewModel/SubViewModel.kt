package com.example.submanager.viewModel

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
// import androidx.compose.runtime.derivedStateOf // Import opzionale, ma utile se lo stato fosse più complesso
import com.example.submanager.model.Subscription
import com.example.submanager.model.Category
import com.example.submanager.ui.theme.AccentColors
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.Icons

class SubViewModel : ViewModel() {

    // ===================================================================
    // 1. STATO FISSO E DATI SORGENTE (Definiti PRIMA di qualsiasi calcolo)
    // ===================================================================

    private val _isDark = mutableStateOf(true)
    val isDark: State<Boolean> = _isDark

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
    // 2. DATI CALCOLATI (Definiti DOPO i dati sorgente)
    // ===================================================================

    // Lo stato delle categorie viene ricalcolato ad ogni accesso
    // (o quando subscriptions.value cambia, grazie a Compose)
    val categoriesState: State<List<Category>>
        get() {
            // Qui accediamo a subscriptions.value e categories che ora sono definiti e inizializzati.
            val groups = subscriptions.value.groupBy { it.category }
            val categoryStats = categories.map { category ->
                // Usa Elvis Operator per garantire una lista vuota e prevenire NullPointerException
                val subs = groups[category.name] ?: emptyList()
                val count = subs.size
                val total = subs.sumOf { it.price }
                category.copy(count = count, total = total)
            }
            // Incapsuliamo in State per fornire un'API reattiva all'esterno
            return mutableStateOf(categoryStats)
        }

    // 3. Metodi di utilità
    fun getTotalMonthly(): Double = subscriptions.value.sumOf { it.price }
    fun getTotalYearly(): Double = getTotalMonthly() * 12

    fun getCategorySubscriptions(categoryName: String): List<Subscription> {
        return subscriptions.value.filter { it.category == categoryName }
    }

    fun getCategoryDetails(categoryName: String): Category? {
        // Legge categoriesState.value, che ricalcola al volo
        return categoriesState.value.find { it.name == categoryName }
    }
}