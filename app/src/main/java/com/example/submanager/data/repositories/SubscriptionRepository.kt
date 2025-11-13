package com.example.submanager.data.repositories

import com.example.submanager.data.database.SubscriptionDAOs
import com.example.submanager.data.database.toEntity
import com.example.submanager.data.database.toSubscription
import com.example.submanager.data.models.Subscription
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Repository per la gestione delle Subscriptions
 */
class SubscriptionRepository(
    private val subscriptionDao: SubscriptionDAOs
) {
    /**
     * Flow reattivo di tutte el subscripitions
     */
    val subscriptions: Flow<List<Subscription>> = subscriptionDao
        .observeAll()
        .map { entities -> entities.map { it.toSubscription() } }


    val totalMonthly: Flow<Double> = subscriptionDao
        .observeTotalMonthly()
        .map { it ?: 0.0 }

    val totalYearly: Flow<Double> = totalMonthly.map { it * 12 }

    suspend fun addSubscription(subscription: Subscription) {
        subscriptionDao.insert(subscription.toEntity())
    }

    suspend fun updateSubscription(subscription: Subscription) {
        subscriptionDao.update(subscription.toEntity())
    }

    suspend fun deleteSubscription(id: Int) {
        subscriptionDao.deleteById(id)
    }

    suspend fun getSubscriptionById(id: Int): Subscription? {
        return subscriptionDao.getById(id)?.toSubscription()
    }

    fun getSubscriptionsByCategory(categoryName: String): Flow<List<Subscription>> {
        return subscriptionDao
            .observeByCategory(categoryName)
            .map { entities -> entities.map { it.toSubscription() } }
    }

    suspend fun updateSubscriptionCategory(oldCategory: String, newCategory: String) {
        // Todo
    }

    suspend fun deleteSubscriptionsByCategory(categoryName: String) {
        subscriptionDao.deleteByCategory(categoryName)
    }
}