package com.example.submanager.utils

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.submanager.MainActivity
import com.example.submanager.R

/**
 * Gestisce la creazione e l'invio delle notifiche
 */
object NotificationHelper {

    private const val CHANNEL_ID = "subscription_reminders"
    private const val CHANNEL_NAME = "Promemoria Abbonamenti"
    private const val CHANNEL_DESCRIPTION = "Notifiche per i rinnovi degli abbonamenti"

    const val ACTION_OPEN_SUBSCRIPTINON = "com.example.submanager.OPEN_SUBSCRIPTION"
    const val EXTRA_SUBSCRIPTION_ID = "subscription_id"

    /**
     * Crea il canale di notifica (richiesto per Android 8.0+)
     */
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
                enableVibration(true)
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Mostra notifica per il rinnovo di un abbonamento
     */
    fun showSubscriptionReminderNotification(
        context: Context,
        subscriptionId: Int,
        subscriptionName: String,
        price: Double,
        daysUntilRenewal: Int
    ) {
        // Verifica permesso notifiche (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
        }

        // Intent per aprire l'app quando si clicca la notifica
        val intent = Intent(context, MainActivity::class.java).apply {
            action = ACTION_OPEN_SUBSCRIPTINON
            putExtra(EXTRA_SUBSCRIPTION_ID, subscriptionId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            subscriptionId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Costruisci la notifica
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_sub)
            .setContentTitle("Rinnovo abbonamento")
            .setContentText("$subscriptionName si rinnova ${if (daysUntilRenewal == 0) "oggi" else "domani"} (€${String.format("%.2f", price)})")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Il tuo abbonamento a $subscriptionName si rinnoverà ${if (daysUntilRenewal == 0) "oggi" else "domani"}.\n\nCosto: €${String.format("%.2f", price)}\n\nTocca per aprire l'app e gestirlo.")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setVibrate(longArrayOf(0, 500, 200, 500))
            .build()

        // Mostra la notifica
        NotificationManagerCompat.from(context).notify(subscriptionId, notification)
    }

    /**
     * Cancella una notifica specifica
     */
    fun cancelNotification(context: Context, subscriptionId: Int) {
        NotificationManagerCompat.from(context).cancel(subscriptionId)
    }
}