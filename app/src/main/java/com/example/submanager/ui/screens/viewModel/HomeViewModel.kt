package com.example.submanager.ui.screens.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.submanager.data.repositories.CategoryRepository
import com.example.submanager.data.repositories.SubscriptionRepository
import com.example.submanager.data.models.Category
import com.example.submanager.data.models.Subscription
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

data class HomeState(
    val subscriptions: List<Subscription> = emptyList(),
    val allSubscriptions: List<Subscription> = emptyList(), // Lista completa non filtrata
    val categories: List<Category> = emptyList(),
    val totalMonthly: Double = 0.0,
    val totalYearly: Double = 0.0,
    val activeCategoriesCount: Int = 0,
    val expiringCount: Int = 0,
    val currentFilterName: String = "Prossimi Rinnovi", // Nome del filtro corrente
    val isLoading: Boolean = false,
    val error: String? = null
)

interface HomeActions {
    fun deleteSubscription(id: Int)
    fun showAll()
    fun showExpiring()
    fun sortByNameAsc()
    fun sortByNameDesc()
    fun sortByPriceAsc()
    fun sortByPriceDesc()
}

class HomeViewModel(
    private val subscriptionRepository: SubscriptionRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _filterName = MutableStateFlow("Prossimi Rinnovi")
    private val _filterFunction = MutableStateFlow<(List<Subscription>) -> List<Subscription>>({ it })

    val state: StateFlow<HomeState> = combine(
        subscriptionRepository.subscriptions,
        subscriptionRepository.totalMonthly,
        subscriptionRepository.totalYearly,
        _filterName,
        _filterFunction
    ) { subscriptions, totalMonthly, totalYearly, filterName, filterFunction ->
        val categoriesWithStats = categoryRepository.getCategoriesWithStats(subscriptions)
        val activeCategoriesCount = categoriesWithStats.count { it.count > 0 }
        val filteredSubs = filterFunction(subscriptions)

        HomeState(
            subscriptions = filteredSubs,
            allSubscriptions = subscriptions,
            categories = categoriesWithStats,
            totalMonthly = totalMonthly,
            totalYearly = totalYearly,
            activeCategoriesCount = activeCategoriesCount,
            expiringCount = calculateExpiringCount(subscriptions),
            currentFilterName = filterName
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = HomeState(isLoading = true)
    )

    val actions = object : HomeActions {

        override fun deleteSubscription(id: Int) {
            viewModelScope.launch {
                subscriptionRepository.deleteSubscription(id)
            }
        }

        override fun showAll() {
            _filterName.update { "Prossimi Rinnovi" }
            _filterFunction.update { { subs -> subs } }
        }

        override fun showExpiring() {
            _filterName.update { "In Scadenza" }
            _filterFunction.update { { subs ->
                val today = LocalDate.now()
                val weekFromNow = today.plusDays(7)
                subs.filter { it.nextBilling in today..weekFromNow }
            }}
        }

        override fun sortByNameAsc() {
            _filterName.update { "Nome A-Z" }
            _filterFunction.update { { subs -> subs.sortedBy { it.name.lowercase() } }}
        }

        override fun sortByNameDesc() {
            _filterName.update { "Nome Z-A" }
            _filterFunction.update { { subs -> subs.sortedByDescending { it.name.lowercase() } }}
        }

        override fun sortByPriceAsc() {
            _filterName.update { "Prezzo Crescente" }
            _filterFunction.update { { subs -> subs.sortedBy { it.price } }}
        }

        override fun sortByPriceDesc() {
            _filterName.update { "Prezzo Decrescente" }
            _filterFunction.update { { subs -> subs.sortedByDescending { it.price } } }
        }
    }

    /**
     * Helper privata per calcolare abbonamenti in scadenza
     * conta abbonamenti che scadono nei prossimi 7 giorni
     */
    private fun calculateExpiringCount(subscriptions: List<Subscription>): Int {
        val today = LocalDate.now()
        val weekFromNow = today.plusDays(7)
        return subscriptions.count { it.nextBilling in today..weekFromNow }
    }
}