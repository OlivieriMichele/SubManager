package com.example.submanager

import android.app.Application
import com.example.submanager.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

/**
 * Application class per inizializzare Koin
 */
class SubManagerApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@SubManagerApplication)
            modules(appModule)
        }
    }
}