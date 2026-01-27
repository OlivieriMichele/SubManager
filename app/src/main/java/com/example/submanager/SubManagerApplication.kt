package com.example.submanager

import android.app.Application
import com.example.submanager.data.repositories.SubscriptionRepository
import com.example.submanager.di.appModule
import com.example.submanager.utils.NotificationHelper
import com.example.submanager.utils.NotificationScheduler
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class SubManagerApplication : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val subscriptionRepository: SubscriptionRepository by inject()

    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)

        startKoin {
            androidLogger()
            androidContext(this@SubManagerApplication)
            modules(appModule)
        }

        NotificationHelper.createNotificationChannel(this)

        applicationScope.launch {
            try {
                val subscriptions = subscriptionRepository.getAllSubscriptions()
                NotificationScheduler.rescheduleAllNotifications(this@SubManagerApplication, subscriptions)
            } catch (e: Exception) {
                // Gestione dell'errore
            }
        }
    }
}