package com.example.submanager.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.submanager.ui.Screen
import com.example.submanager.ui.screens.viewModel.CategoryViewModel
import com.example.submanager.ui.screens.viewModel.SubscriptionViewModel
import com.example.submanager.ui.theme.CategoryIcons
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppFloatingActionButton(
    screen: Screen?,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    var icon = Icons.Default.Add
    var description = ""
    var onClick: () -> Unit = {}

    when (screen) {
        Screen.Home -> {
            icon = Icons.Default.Add
            description = "Aggiungi Abbonamento"
            onClick = { navController.navigate(Screen.AddSubscription) }
        }

        Screen.AddSubscription -> {
            val subscriptionViewModel = koinViewModel<SubscriptionViewModel>()
            val state by subscriptionViewModel.state.collectAsStateWithLifecycle()

            icon = Icons.Default.Check
            description = "Salva"
            onClick = {
                if (!state.isSaving) {
                    subscriptionViewModel.actions.saveSubscription {
                        navController.popBackStack()
                    }
                }
            }
        }

        is Screen.ViewSubscription -> {
            val subscriptionViewModel = koinViewModel<SubscriptionViewModel>()
            val state by subscriptionViewModel.state.collectAsStateWithLifecycle()

            if (state.isEditing) {
                icon = Icons.Default.Check
                description = "Salva"
                onClick = {
                    if (!state.isSaving) {
                        subscriptionViewModel.actions.saveSubscription {
                            navController.popBackStack()
                        }
                    }
                }
            } else {
                icon = Icons.Default.Edit
                description = "Modifica"
                onClick = { subscriptionViewModel.actions.setEditMode(true) }
            }
        }

        Screen.Categories -> {
            icon = Icons.Default.Add
            description = "Aggiungi Categoria"
            onClick = { navController.navigate(Screen.NewCategory) }
        }

        Screen.NewCategory -> {
            val categoryViewModel = koinViewModel<CategoryViewModel>()
            val state by categoryViewModel.categoryFormState.collectAsStateWithLifecycle()
            icon = Icons.Default.Save
            description = "Salva Categoria"
            onClick = {
                val selectedIcon = CategoryIcons.availableIcons[state.selectedIconIndex]
                categoryViewModel.saveCategoryWithIcon(selectedIcon) {
                    navController.popBackStack()
                }
            }
        }

        else -> return
    }

    FloatingActionButton(
        onClick = onClick,
        shape = CircleShape,
        modifier = modifier
            .size(56.dp)
            .background(MaterialTheme.colorScheme.primary, CircleShape)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = description,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}