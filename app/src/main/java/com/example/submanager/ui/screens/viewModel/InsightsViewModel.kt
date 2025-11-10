package com.example.submanager.ui.screens.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.submanager.data.repositories.CategoryRepository
import com.example.submanager.data.repositories.SubscriptionRepository
import com.example.submanager.data.models.Category
import com.example.submanager.data.models.MonthData
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class InsightsState(
    val totalMonthly: Double = 0.0,
    val totalYearly: Double = 0.0,
    val lastMonthTotal: Double = 0.0, // TODO: Calcolare da storico
    val percentageChange: Double = 0.0,
    val averagePerService: Double = 0.0,
    val categories: List<Category> = emptyList(),
    val mostExpensiveSubscription: String? = null,
    val mostExpensiveCategory: String? = null,
    val potentialSavings: Double = 0.0, // TODO: Logica di calcolo
    val last5MonthsData: List<MonthData> = emptyList(), // TODO: Da database
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * InsightsViewModel - Gestisce statistiche e analytics
 *
 * RESPONSABILITÀ:
 * - Calcola tutte le statistiche mostrate in InsightsScreen
 * - Medie, aggregazioni, confronti e Trend mensili
 *
 * TODO: con Room DB, da aggiungere:
 * - Storico mensile subscriptions
 * - Calcolo trend reale
 * - Potenziali risparmi basati su subscriptions non usate
 */
class InsightsViewModel(
    private val subscriptionRepository: SubscriptionRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    val state: StateFlow<InsightsState> = combine(
        subscriptionRepository.subscriptions,
        subscriptionRepository.totalMonthly,
        subscriptionRepository.totalYearly
    ) { subscriptions, totalMonthly, totalYearly ->

        val categoriesWithStats = categoryRepository.getCategoriesWithStats(subscriptions)
        val activeServicesCount = subscriptions.size
        val averagePerService = if (activeServicesCount > 0) {
            totalMonthly / activeServicesCount
        } else { 0.0 }

        val mostExpensiveSub = subscriptions.maxByOrNull { it.price }?.name
        val mostExpensiveCat = categoriesWithStats.maxByOrNull { it.total }?.name

        // TODO: Calcolare da storico reale quando hai Room DB
        val lastMonthTotal = 85.85
        val percentageChange = if (lastMonthTotal > 0) {
            ((totalMonthly - lastMonthTotal) / lastMonthTotal) * 100
        } else {
            0.0
        }

        // TODO: Implementare logica di calcolo risparmi
        val potentialSavings = 59.88

        // TODO: Caricare da database
        val monthsData = listOf(
            MonthData("Giu", 87.20),
            MonthData("Lug", 87.20),
            MonthData("Ago", 87.20),
            MonthData("Set", 87.20),
            MonthData("Ott", 87.20)
        )

        InsightsState(
            totalMonthly = totalMonthly,
            totalYearly = totalYearly,
            lastMonthTotal = lastMonthTotal,
            percentageChange = percentageChange,
            averagePerService = averagePerService,
            categories = categoriesWithStats,
            mostExpensiveSubscription = mostExpensiveSub,
            mostExpensiveCategory = mostExpensiveCat,
            potentialSavings = potentialSavings,
            last5MonthsData = monthsData
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = InsightsState(isLoading = true)
    )
}