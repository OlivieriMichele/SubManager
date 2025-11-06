package com.example.submanager.di

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.example.submanager.data.local.PreferencesManager
import com.example.submanager.data.repositories.ThemeRepository
import com.example.submanager.ui.screens.ThemeViewModel
// import com.example.submanager.data.repositories.CategoryRepository
// import com.example.submanager.data.repositories.SubscriptionRepository
// import com.example.submanager.ui.screens.home.HomeViewModel
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

    // ========== DATA LAYER ==========

    // DataStore singleton
    single { androidContext().dataStore }

    // Preferences Manager (gestisce operazioni DataStore)
    single { PreferencesManager(get()) }

    // Repositories (singoli per dominio)
    // single { SubscriptionRepository(get()) }
    // single { CategoryRepository(get()) }
    single { ThemeRepository(get()) }

    // ========== PRESENTATION LAYER ==========

    // ViewModels (uno per schermata/feature)
    // viewModel { HomeViewModel(get(), get()) }
    viewModel { ThemeViewModel(get()) }
}