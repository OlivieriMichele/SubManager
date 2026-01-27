package com.example.submanager.utils

import android.content.Context
import android.util.Log
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.submanager.data.models.Subscription
import com.example.submanager.workers.SubscriptionReminderWorker
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * Gestisce la pianificazione delle notifiche per gli abbonamenti
 */
object NotificationScheduler {

    private const val NOTIFICATION_HOUR = 13 // Ora del giorno per le notifiche
    private const val NOTIFICATION_MINUTE = 30 // Minuti (13:30)
    private const val DAYS_BEFORE_RENEWAL = 1L // Giorni prima del rinnovo

    /**
     * Pianifica una notifica per un abbonamento
     */
    fun scheduleNotification(context: Context, subscription: Subscription) {
        val workManager = WorkManager.getInstance(context)

        // Calcola quando inviare la notifica (1 giorno prima alle 10:00)
        val notificationDateTime = calculateNotificationTime(subscription.nextBilling)
        val now = LocalDateTime.now()

        // Se la data è nel passato, non pianificare
        if (notificationDateTime.isBefore(now)) {
            Log.d("NotificationScheduler", "Data nel passato, skip notifica per: ${subscription.name}")
            return
        }

        val delay = Duration.between(now, notificationDateTime)

        Log.d("NotificationScheduler", """
            Pianificazione notifica:
            - Abbonamento: ${subscription.name}
            - Rinnovo: ${subscription.nextBilling}
            - Notifica: $notificationDateTime
            - Delay: ${delay.toHours()}h 
        """.trimIndent())

        // Crea il WorkRequest
        val workData = Data.Builder()
            .putInt("subscription_id", subscription.id)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<SubscriptionReminderWorker>()
            .setInitialDelay(delay)
            .setInputData(workData)
            .addTag("subscription_${subscription.id}")
            .build()

        // Pianifica il work (sostituisce eventuali work esistenti con lo stesso nome)
        workManager.enqueueUniqueWork(
            "reminder_${subscription.id}",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )

        Log.d("NotificationScheduler", "Notifica pianificata per: ${subscription.name}")
    }

    /**
     * Calcola quando mostrare la notifica
     * (1 giorno prima del rinnovo alle 10:30)
     */
    private fun calculateNotificationTime(renewalDate: LocalDate): LocalDateTime {
        val notificationDate = renewalDate.minusDays(DAYS_BEFORE_RENEWAL)
        return LocalDateTime.of(notificationDate, LocalTime.of(NOTIFICATION_HOUR, NOTIFICATION_MINUTE))
    }

    /**
     * Cancella la notifica pianificata per un abbonamento
     */
    fun cancelNotification(context: Context, subscriptionId: Int) {
        val workManager = WorkManager.getInstance(context)
        workManager.cancelUniqueWork("reminder_$subscriptionId")

        // Cancella anche la notifica attiva se presente
        NotificationHelper.cancelNotification(context, subscriptionId)

        Log.d("NotificationScheduler", "Notifica cancellata per subscription: $subscriptionId")
    }

    /**
     * Ripianifica tutte le notifiche (utile dopo un riavvio)
     */
    suspend fun rescheduleAllNotifications(
        context: Context,
        subscriptions: List<Subscription>
    ) {
        Log.d("NotificationScheduler", "Ripianificazione di ${subscriptions.size} notifiche...")

        subscriptions.forEach { subscription ->
            scheduleNotification(context, subscription)
        }

        Log.d("NotificationScheduler", "Ripianificazione completata")
    }
}