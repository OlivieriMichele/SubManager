package com.example.submanager

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.submanager.data.models.Theme
import com.example.submanager.ui.SubNavigation
import com.example.submanager.ui.composable.AppFloatingActionButton
import com.example.submanager.ui.composable.AppHeader
import com.example.submanager.ui.getCurrentScreen
import com.example.submanager.ui.screens.viewModel.ThemeViewModel
import com.example.submanager.ui.theme.SubManagerTheme
import com.example.submanager.utils.NotificationHelper
import org.koin.androidx.compose.koinViewModel

class MainActivity : FragmentActivity() {

    private var pendingSubscriptionId: Int? by mutableStateOf(null)

    private val requestNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.d("MainActivity", "Permesso notifiche concesso")
        } else {
            Log.w("MainActivity", "Permesso notifiche negato")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Richiedi permesso notifiche se necessario (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        // Gestisci intent dalla notifica
        handleNotificationIntent(intent)

        setContent {
            val themeViewModel = koinViewModel<ThemeViewModel>()
            val themeState by themeViewModel.state.collectAsStateWithLifecycle()
            val navController = rememberNavController()

            val darkTheme = when (themeState.theme) {
                Theme.Dark -> true
                Theme.Light -> false
                Theme.System -> isSystemInDarkTheme()
            }

            SubManagerTheme(darkTheme, dynamicColor = false) {
                val currentBackStackEntry by navController.currentBackStackEntryAsState()
                val currentScreen = currentBackStackEntry?.getCurrentScreen()

                Scaffold(
                    containerColor = MaterialTheme.colorScheme.background,
                    topBar = {
                        AppHeader(
                            screen = currentScreen,
                            navController = navController
                        )
                    },
                    floatingActionButton = {
                        AppFloatingActionButton(
                            screen = currentScreen,
                            navController = navController
                        )
                    }
                ) { innerPadding ->
                    SubNavigation(
                        navController = navController,
                        themeViewModel = themeViewModel,
                        pendingSubscriptionId = pendingSubscriptionId,
                        onSubscriptionNavigated = { pendingSubscriptionId = null },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    // Gestisci nuovi intent quando l'app è già aperta (singleTop)
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent) // Importante per aggiornare l'intent corrente
        handleNotificationIntent(intent)
    }

    // Estrae l'ID della subscription dall'intent
    private fun handleNotificationIntent(intent: Intent?) {
        if (intent?.action == NotificationHelper.ACTION_OPEN_SUBSCRIPTINON) {
            val subscriptionId = intent.getIntExtra(NotificationHelper.EXTRA_SUBSCRIPTION_ID, -1)

            if (subscriptionId != -1) {
                Log.d("MainActivity", "📱 Notifica cliccata - Apertura subscription: $subscriptionId")
                pendingSubscriptionId = subscriptionId
            } else {
                Log.w("MainActivity", "⚠️ Intent ricevuto ma ID subscription non valido")
            }
        }
    }
}