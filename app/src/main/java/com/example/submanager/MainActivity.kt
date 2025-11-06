package com.example.submanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.submanager.ui.SubNavigation
import com.example.submanager.ui.getCurrentScreen
import com.example.submanager.ui.screens.AppFloatingActionButton
import com.example.submanager.ui.screens.AppHeader
import com.example.submanager.ui.screens.ThemeViewModel
import com.example.submanager.ui.theme.SubManagerTheme
import com.example.submanager.viewModel.SubViewModel
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: SubViewModel by viewModels() // Temporaneo, rimosso nelle prossime fasi di refacotring

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeViewModel = koinViewModel<ThemeViewModel>()
            val isDark by themeViewModel.isDarkMode.collectAsStateWithLifecycle()
            val navController = rememberNavController()

            SubManagerTheme(darkTheme = isDark, dynamicColor = false) {
                val currentBackStackEntry by navController.currentBackStackEntryAsState()
                val currentScreen = currentBackStackEntry?.getCurrentScreen()

                Scaffold(
                    containerColor = MaterialTheme.colorScheme.background,
                    topBar = {
                        AppHeader(
                            screen = currentScreen,
                            viewModel = viewModel,
                            themeViewModel = themeViewModel,
                            navController = navController
                        )
                    },
                    floatingActionButton = {
                        AppFloatingActionButton(
                            screen = currentScreen,
                            viewModel = viewModel,
                            navController = navController
                        )
                    }
                ) { innerPadding ->
                    SubNavigation(navController, viewModel, Modifier.padding(innerPadding))
                }
            }
        }
    }
}