package com.example.submanager.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.example.submanager.model.Subscription
import com.example.submanager.ui.theme.AccentColors
import java.time.LocalDate

class SubscriptionManager {

    private val _subscriptions = mutableStateOf(
        listOf(
            Subscription(1, "Netflix", 12.99, AccentColors.pastelPurple, LocalDate.of(2025,10,30), "Intrattenimento"),
            Subscription(2, "Spotify", 9.99, AccentColors.pastelBlue, LocalDate.of(2025,10,15), "Intrattenimento"),
            Subscription(3, "Adobe CC", 24.99, AccentColors.pastelIndigo, LocalDate.of(2025,10,28), "Software"),
            Subscription(4, "Amazon Prime", 4.99, AccentColors.pastelPink, LocalDate.of(2025,11,5), "Intrattenimento"),
            Subscription(5, "GitHub Pro", 4.00, AccentColors.pastelYellow, LocalDate.of(2025,11,12), "Software"),
            Subscription(6, "Planet Fitness", 29.99, AccentColors.pastelGreen, LocalDate.of(2025,11,30), "Fitness")
        )
    )
    val subscriptions: State<List<Subscription>> = _subscriptions

    fun getTotalMonthly(): Double = subscriptions.value.sumOf { it.price }

    fun getTotalYearly(): Double = getTotalMonthly() * 12

    fun getCategorySubscriptions(categoryName: String): List<Subscription> {
        return subscriptions.value.filter { it.category == categoryName }
    }

    fun getSubscriptionById(id: Int): Subscription? {
        return subscriptions.value.find { it.id == id }
    }

    fun addSubscription(subscription: Subscription) {
        _subscriptions.value += subscription
    }

    fun updateSubscription(subscription: Subscription) {
        _subscriptions.value = _subscriptions.value.map {
            if (it.id == subscription.id) subscription else it
        }
    }

    fun deleteSubscription(id: Int) {
        _subscriptions.value = _subscriptions.value.filter { it.id != id }
    }

    fun updateSubscriptionCategory(oldCategory: String, newCategory: String) {
        _subscriptions.value = _subscriptions.value.map { subscription ->
            if (subscription.category == oldCategory) {
                subscription.copy(category = newCategory)
            } else {
                subscription
            }
        }
    }

    fun deleteSubscriptionsByCategory(categoryName: String) {
        _subscriptions.value = _subscriptions.value.filter { it.category != categoryName }
    }
}