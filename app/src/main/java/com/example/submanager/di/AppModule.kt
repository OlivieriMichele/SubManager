package com.example.submanager.di

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.example.submanager.data.local.PreferencesManager
import com.example.submanager.data.repositories.CategoryRepository
import com.example.submanager.data.repositories.SubscriptionRepository
import com.example.submanager.data.repositories.ThemeRepository
import com.example.submanager.ui.screens.ThemeViewModel
import com.example.submanager.ui.screens.categories.CategoryViewModel
import com.example.submanager.ui.screens.home.HomeViewModel
import com.example.submanager.ui.screens.insights.InsightsViewModel
import com.example.submanager.ui.screens.subscription.SubscriptionViewModel
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

    single { androidContext().dataStore }

    single { PreferencesManager(get()) }
    single { SubscriptionRepository() }
    single { CategoryRepository() }
    single { ThemeRepository(get()) }

    viewModel { ThemeViewModel(get()) }
    viewModel { HomeViewModel(get(), get()) }
    viewModel { CategoryViewModel(get(), get()) }
    viewModel { SubscriptionViewModel(get(), get()) }
    viewModel { InsightsViewModel(get(), get()) }
}