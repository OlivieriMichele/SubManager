package com.example.submanager

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.submanager.data.models.Theme
import com.example.submanager.ui.Screen
import com.example.submanager.ui.SubNavigation
import com.example.submanager.ui.composable.AppFloatingActionButton
import com.example.submanager.ui.composable.AppHeader
import com.example.submanager.ui.getCurrentScreen
import com.example.submanager.ui.screens.viewModel.ThemeViewModel
import com.example.submanager.ui.theme.SubManagerTheme
import org.koin.androidx.compose.koinViewModel

class MainActivity : FragmentActivity() {

    // Request launcher per permesso notifiche (Android 13+)
    private val requestNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permesso concesso
        } else {
            // Permesso negato - puoi mostrare un messaggio all'utente
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
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
        handleNotificationIntent(intent)
    }

    // Gestisci nuovi intent quando l'app è già aperta
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleNotificationIntent(intent)
    }

    // Apri la schermata dell'abbonamento quando si clicca la notifica
    private fun handleNotificationIntent(intent: Intent?) {
        if (intent?.action == "com.example.submanager.OPEN_SUBSCRIPTION") {
            val subscriptionId = intent.getIntExtra("subscription_id", -1)
            if (subscriptionId != -1) {
                // L'intent contiene l'ID dell'abbonamento
                // La navigazione verrà gestita in SubNavigation
                // Puoi salvare questo ID in un ViewModel o usare un deep link
            }
        }
    }
}