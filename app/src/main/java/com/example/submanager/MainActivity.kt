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
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.submanager.ui.screens.AppFloatingActionButton
import com.example.submanager.ui.screens.AppHeader
import com.example.submanager.ui.theme.SubManagerTheme
import com.example.submanager.viewModel.SubViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: SubViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDark = viewModel.isDark.value
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
                            navController = navController
                        )
                    },
                    floatingActionButton = {
                        AppFloatingActionButton(
                            screen = currentScreen,
                            viewModel = viewModel,
                            onAdd = { navController.navigate(Screen.AddSubscription) },
                            onEdit = { viewModel.setEditingMode(true) },
                            onSave = { viewModel.triggerSave() },
                            onAddCategory = { navController.navigate(Screen.NewCategory) },
                            onSaveCategory = { viewModel.triggerSaveCategory() }
                        )
                    }
                ) { innerPadding ->
                    SubNavigation(navController, viewModel, Modifier.padding(innerPadding))
                }
            }
        }
    }
}