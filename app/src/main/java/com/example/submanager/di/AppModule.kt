package com.example.submanager.di

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.example.submanager.data.database.SubManagerDatabase
import com.example.submanager.data.local.PreferencesManager
import com.example.submanager.data.repositories.AuthRepository
import com.example.submanager.data.repositories.CategoryRepository
import com.example.submanager.data.repositories.SubscriptionRepository
import com.example.submanager.data.repositories.ThemeRepository
import com.example.submanager.ui.screens.viewModel.AuthViewModel
import com.example.submanager.ui.screens.viewModel.ThemeViewModel
import com.example.submanager.ui.screens.viewModel.CategoryViewModel
import com.example.submanager.ui.screens.viewModel.HomeViewModel
import com.example.submanager.ui.screens.viewModel.InsightsViewModel
import com.example.submanager.ui.screens.viewModel.SubscriptionViewModel
import com.google.firebase.auth.FirebaseAuth
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val Context.dataStore by preferencesDataStore(name = "submanager_prefs")

/**
 * 1. DataStore e Preferences
 * 2. Repositories (layer dati)
 * 3. ViewModels (layer presentazione)
 */
val appModule = module {
    // DataStore per preferences
    single { get<Context>().dataStore }
    single { PreferencesManager(get()) }

    // Firebase
    single { FirebaseAuth.getInstance() }

    // Room Database (singleton)
    single { SubManagerDatabase.getDatabase(androidContext()) }

    // DAOs
    single { get<SubManagerDatabase>().subscriptionDao() }
    single { get<SubManagerDatabase>().categoryDao() }

    // Repositories
    single { SubscriptionRepository(get()) }
    single { CategoryRepository(get(),get()) } // riceve subscriptionDao
    single { ThemeRepository(get()) } // riceve subscription e category Dao
    single { AuthRepository(get()) }

    // ViewModels
    viewModel { AuthViewModel(get()) }
    viewModel { ThemeViewModel(get()) }
    viewModel { HomeViewModel(get(), get()) }
    viewModel { CategoryViewModel(get(), get()) }
    viewModel { SubscriptionViewModel(get(), get()) }
    viewModel { InsightsViewModel(get(), get()) }
}