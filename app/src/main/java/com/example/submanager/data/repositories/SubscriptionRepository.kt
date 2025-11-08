package com.example.submanager.data.repositories

import androidx.compose.ui.graphics.Color
import com.example.submanager.model.Subscription
import com.example.submanager.ui.theme.AccentColors
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

/**
 * Repository per la gestione delle Subscriptions
 */
class SubscriptionRepository() {
    // Todo: State interno (in futuro sarà Room Database)
    private val _subscriptions = MutableStateFlow(
        listOf(
            Subscription(1, "Netflix", 12.99, AccentColors.pastelPurple, LocalDate.of(2025,10,30), "Intrattenimento"),
            Subscription(2, "Spotify", 9.99, AccentColors.pastelBlue, LocalDate.of(2025,10,15), "Intrattenimento"),
            Subscription(3, "Adobe CC", 24.99, AccentColors.pastelIndigo, LocalDate.of(2025,10,28), "Software"),
            Subscription(4, "Amazon Prime", 4.99, AccentColors.pastelPink, LocalDate.of(2025,11,5), "Intrattenimento"),
            Subscription(5, "GitHub Pro", 4.00, AccentColors.pastelYellow, LocalDate.of(2025,11,12), "Software"),
            Subscription(6, "Planet Fitness", 29.99, AccentColors.pastelGreen, LocalDate.of(2025,11,30), "Fitness"),
            Subscription(7, "Affitto", 250.00, Color.Magenta.copy(alpha = 0.33f), LocalDate.of(2025,11,16), "Casa")
        )
    )

    // Espone un Flow osservabile (reattivo)
    val subscriptions: StateFlow<List<Subscription>> = _subscriptions.asStateFlow()

    // Flow derivati (calcolati automaticamente quando subscriptions cambia)
    val totalMonthly: Flow<Double> = subscriptions.map { list ->
        list.sumOf { it.price }
    }

    val totalYearly: Flow<Double> = totalMonthly.map { it * 12 }

    suspend fun addSubscription(subscription: Subscription) {
        _subscriptions.value = _subscriptions.value + subscription
        // Todo: Salva su Room DB
    }

    suspend fun updateSubscription(subscription: Subscription) {
        _subscriptions.value = _subscriptions.value.map {
            if (it.id == subscription.id) subscription else it
        }
        // Todo: Aggiorna su Room DB
    }

    suspend fun deleteSubscription(id: Int) {
        _subscriptions.value = _subscriptions.value.filterNot { it.id == id }
        // Todo: Elimina da Room DB
    }

    fun getSubscriptionById(id: Int): Subscription? {
        return _subscriptions.value.find { it.id == id }
    }

    fun getSubscriptionsByCategory(categoryName: String): List<Subscription> {
        return _subscriptions.value.filter { it.category == categoryName }
    }

    suspend fun updateSubscriptionCategory(oldCategory: String, newCategory: String) {
        _subscriptions.value = _subscriptions.value.map { subscription ->
            if (subscription.category == oldCategory) {
                subscription.copy(category = newCategory)
            } else {
                subscription
            }
        }
    }

    suspend fun deleteSubscriptionsByCategory(categoryName: String) {
        _subscriptions.value = _subscriptions.value.filterNot { it.category == categoryName }
    }
}