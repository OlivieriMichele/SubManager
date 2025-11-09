package com.example.submanager.ui.screens.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.submanager.data.repositories.CategoryRepository
import com.example.submanager.data.repositories.SubscriptionRepository
import com.example.submanager.model.Category
import com.example.submanager.model.Subscription
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeState(
    val subscriptions: List<Subscription> = emptyList(),
    val categories: List<Category> = emptyList(),
    val totalMonthly: Double = 0.0,
    val totalYearly: Double = 0.0,
    val activeCategoriesCount: Int = 0,
    val expiringCount: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)

interface HomeActions {
    fun refreshData()
    fun deleteSubscription(id: Int)
}

class HomeViewModel(
    private val subscriptionRepository: SubscriptionRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    val state: StateFlow<HomeState> = combine(
        subscriptionRepository.subscriptions,
        subscriptionRepository.totalMonthly,
        subscriptionRepository.totalYearly
    ) { subscriptions, totalMonthly, totalYearly ->
        val categoriesWithStats = categoryRepository.getCategoriesWithStats(subscriptions)
        val activeCategoriesCount = categoriesWithStats.count { it.count > 0 }

        HomeState(
            subscriptions = subscriptions,
            categories = categoriesWithStats,
            totalMonthly = totalMonthly,
            totalYearly = totalYearly,
            activeCategoriesCount = activeCategoriesCount,
            expiringCount = calculateExpiringCount(subscriptions)
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = HomeState(isLoading = true)
    )

    val actions = object : HomeActions {
        override fun refreshData() {
            // TODO: Quando avrò Room DB, ricara i dati
            // Per ora non fa nulla perché i dati sono già reattivi
            viewModelScope.launch {}
        }

        // Todo: sposta in subscription
        override fun deleteSubscription(id: Int) {
            viewModelScope.launch {
                subscriptionRepository.deleteSubscription(id)
                // Lo state si aggiorna automaticamente perché osserva il repository!
            }
        }
    }

    /**
     * Helper privata per calcolare abbonamenti in scadenza
     *
     * TODO: Implementa logica vera basata su nextBilling
     * Es: conta abbonamenti che scadono nei prossimi 7 giorni
     */
    private fun calculateExpiringCount(subscriptions: List<Subscription>): Int {
        // Placeholder: ritorna valore fisso
        // val today = LocalDate.now()
        // val weekFromNow = today.plusDays(7)
        // return subscriptions.count { it.nextBilling in today..weekFromNow }
        return 2
    }
}