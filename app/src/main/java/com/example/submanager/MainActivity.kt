package com.example.submanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.submanager.ui.screens.AppFloatingActionButton
import com.example.submanager.ui.screens.FabType
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
                val isEditing by viewModel.isEditingState
                val currentScreen = currentBackStackEntry?.getCurrentScreen()

                // Reset editing mode quando cambi schermata
                LaunchedEffect(currentScreen) {
                    if (currentScreen !is Screen.ViewSubscription) {
                        viewModel.resetEditingMode()
                    }
                }

                // Determina tipo e azione del FAB
                val fabType = when (currentScreen) {
                    is Screen.Home -> FabType.ADD
                    is Screen.AddSubscription -> FabType.SAVE
                    is Screen.ViewSubscription -> if (isEditing) FabType.SAVE else FabType.EDIT
                    else -> FabType.NONE
                }

                val fabAction = when (currentScreen) {
                    is Screen.Home -> {{ navController.navigate(Screen.AddSubscription) }}
                    is Screen.AddSubscription -> {{ viewModel.triggerSave() }}
                    is Screen.ViewSubscription -> {
                        if (isEditing) {{ viewModel.triggerSave() }}
                        else {{ viewModel.setEditingMode(true) }}
                    }
                    else -> {{}}
                }

                Scaffold(
                    containerColor = MaterialTheme.colorScheme.background,
                    floatingActionButton = {
                        AppFloatingActionButton(
                            fabType = fabType,
                            onClick = fabAction
                        )
                    }
                ) { innerPadding ->
                    SubNavigation(navController, viewModel, Modifier.padding(innerPadding))
                }
            }
        }
    }
}