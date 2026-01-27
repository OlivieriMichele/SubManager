package com.example.submanager.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.submanager.data.repositories.SubscriptionRepository
import com.example.submanager.utils.NotificationHelper
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.LocalDate
import java.time.temporal.ChronoUnit

/**
 * Worker che gestisce l'invio delle notifiche per i rinnovi abbonamenti
 */
class SubscriptionReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    private val subscriptionRepository: SubscriptionRepository by inject()

    override suspend fun doWork(): Result {
        return try {
            Log.d("ReminderWorker", "Worker avviato")

            val subscriptionId = inputData.getInt("subscription_id", -1)

            if (subscriptionId == -1) {
                Log.e("ReminderWorker", "ID abbonamento non valido")
                return Result.failure()
            }

            // Recupera l'abbonamento dal database
            val subscription = subscriptionRepository.getSubscriptionById(subscriptionId)

            if (subscription == null) {
                Log.e("ReminderWorker", "Abbonamento non trovato: $subscriptionId")
                return Result.failure()
            }

            // Calcola i giorni mancanti al rinnovo
            val today = LocalDate.now()
            val daysUntilRenewal = ChronoUnit.DAYS.between(today, subscription.nextBilling).toInt()

            Log.d("ReminderWorker", "Abbonamento: ${subscription.name}, giorni mancanti: $daysUntilRenewal")

            // Mostra notifica solo se manca 1 giorno o è il giorno stesso
            if (daysUntilRenewal in 0..1) {
                NotificationHelper.showSubscriptionReminderNotification(
                    context = applicationContext,
                    subscriptionId = subscription.id,
                    subscriptionName = subscription.name,
                    price = subscription.price,
                    daysUntilRenewal = daysUntilRenewal
                )
                Log.d("ReminderWorker", "Notifica inviata per: ${subscription.name}")
            } else {
                Log.d("ReminderWorker", "Troppo presto per notificare (giorni: $daysUntilRenewal)")
            }

            Result.success()

        } catch (e: Exception) {
            Log.e("ReminderWorker", "Errore nel worker", e)
            Result.failure()
        }
    }
}