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
                val currentRoute by navController.currentBackStackEntryAsState()
                val route = currentRoute?.destination?.route
                val isEditing by viewModel.isEditingState

                // Reset editing mode quando cambi schermata
                LaunchedEffect(route) {
                    if (route != null && !route.startsWith("view_subscription/")) {
                        viewModel.resetEditingMode()
                    }
                }

                val fabType = NavigationFabHelper.getFabType(route, isEditing)
                val fabAction = NavigationFabHelper.getFabAction(
                    route = route,
                    isEditing = isEditing,
                    onAdd = {navController.navigate(Screen.AddSubscription.route)},
                    onEdit = {viewModel.setEditingMode(true) },
                    onSave = {viewModel.triggerSave() }
                )

                Scaffold(
                    containerColor = MaterialTheme.colorScheme.background,
                    floatingActionButton = {
                        AppFloatingActionButton(
                            fabType = fabType,
                            onClick = fabAction
                        )
                    }
                ) {innerPadding ->
                    SubNavigation(navController, viewModel, Modifier.padding(innerPadding))
                }
            }
        }
    }
}