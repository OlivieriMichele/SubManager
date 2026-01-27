package com.example.submanager.data.repositories

import android.content.Context
import com.example.submanager.data.database.SubscriptionDAOs
import com.example.submanager.data.database.toEntity
import com.example.submanager.data.database.toSubscription
import com.example.submanager.data.models.Subscription
import com.example.submanager.utils.NotificationScheduler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Repository per la gestione delle Subscriptions
 */
class SubscriptionRepository(
    private val subscriptionDao: SubscriptionDAOs,
    private val context: Context
) {
    /**
     * Flow reattivo di tutte le subscriptions
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
        // Pianifica notifica per il nuovo abbonamento
        NotificationScheduler.scheduleNotification(context, subscription)
    }

    suspend fun updateSubscription(subscription: Subscription) {
        subscriptionDao.update(subscription.toEntity())
        // Ripianifica notifica (potrebbero essere cambiati nome/prezzo/data)
        NotificationScheduler.cancelNotification(context, subscription.id)
        NotificationScheduler.scheduleNotification(context, subscription)
    }

    suspend fun deleteSubscription(id: Int) {
        subscriptionDao.deleteById(id)
        // Cancella notifica pianificata
        NotificationScheduler.cancelNotification(context, id)
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
        // Prima cancella le notifiche
        val subscriptionsToDelete = subscriptionDao.observeByCategory(categoryName)
        subscriptionsToDelete.collect { entities ->
            entities.forEach { entity ->
                NotificationScheduler.cancelNotification(context, entity.id)
            }
        }
        subscriptionDao.deleteByCategory(categoryName)
    }

    // Metodo helper per ottenere tutte le subscriptions (non Flow)
    suspend fun getAllSubscriptions(): List<Subscription> {
        return subscriptionDao.getAll().map { it.toSubscription() }
    }
}