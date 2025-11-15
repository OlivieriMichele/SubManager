package com.example.submanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.submanager.data.models.Theme
import com.example.submanager.ui.Screen
import com.example.submanager.ui.SubNavigation
import com.example.submanager.ui.composable.AppFloatingActionButton
import com.example.submanager.ui.composable.AppHeader
import com.example.submanager.ui.getCurrentScreen
import com.example.submanager.ui.screens.viewModel.AuthViewModel
import com.example.submanager.ui.screens.viewModel.ThemeViewModel
import com.example.submanager.ui.theme.SubManagerTheme
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeViewModel = koinViewModel<ThemeViewModel>()
            val authViewModel = koinViewModel<AuthViewModel>()

            val themeState by themeViewModel.state.collectAsStateWithLifecycle()
            val authState by authViewModel.state.collectAsStateWithLifecycle()
            val navController = rememberNavController()

            // Controlla autenticazione all'avvio
            LaunchedEffect(Unit) {
                authViewModel.actions.checkAuth()
            }

            val darkTheme = when (themeState.theme) {
                Theme.Dark -> true
                Theme.Light -> false
                Theme.System -> isSystemInDarkTheme()
            }

            SubManagerTheme(darkTheme, dynamicColor = false) {
                val currentBackStackEntry by navController.currentBackStackEntryAsState()
                val currentScreen = currentBackStackEntry?.getCurrentScreen()

                val showBars = currentScreen !is Screen.Login && currentScreen !is Screen.Register

                Scaffold(
                    containerColor = MaterialTheme.colorScheme.background,
                    topBar = {
                        if (showBars) {
                            AppHeader(
                                screen = currentScreen,
                                navController = navController
                            )
                        }
                    },
                    floatingActionButton = {
                        if (showBars) {
                            AppFloatingActionButton(
                                screen = currentScreen,
                                navController = navController
                            )
                        }
                    }
                ) { innerPadding ->
                    SubNavigation(
                        navController = navController,
                        modifier = Modifier.padding(if (showBars) innerPadding else PaddingValues()),
                        startDestination = if (authState.isAuthenticated) Screen.Home else Screen.Login
                    )
                }
            }
        }
    }
}