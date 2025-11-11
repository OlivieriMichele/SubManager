package com.example.submanager.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.submanager.ui.Screen
import com.example.submanager.ui.composable.components.DeleteButton
import com.example.submanager.ui.composable.components.DeleteConfirmationDialog
import com.example.submanager.ui.composable.components.HeaderActionButtons
import com.example.submanager.ui.composable.components.ThemeSelectionDialog
import com.example.submanager.ui.screens.viewModel.ThemeViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppHeader(
    screen: Screen?,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val themeViewModel = koinViewModel<ThemeViewModel>()
    val themeState by themeViewModel.state.collectAsStateWithLifecycle()

    // State management
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }

    // Screen configuration
    val title = when (screen) {
        Screen.Home -> "I Miei Abbonamenti"
        Screen.AddSubscription -> "Nuovo Abbonamento"
        Screen.Categories -> "Categorie"
        Screen.NewCategory -> "Nuova Categoria"
        Screen.Insights -> "Statistiche"
        is Screen.ViewSubscription -> "Dettaglio"
        is Screen.CategoryDetail -> screen.categoryName
        else -> return // nessun header
    }

    val showBackButton = screen != Screen.Home
    val showHomeActions = screen == Screen.Home
    val showDeleteButton = screen is Screen.CategoryDetail || screen is Screen.ViewSubscription

    // Dialogs
    if (showDeleteDialog) {
        DeleteConfirmationDialog(
            screen = screen,
            navController = navController,
            onDismiss = { showDeleteDialog = false }
        )
    }

    if (showThemeDialog) {
        ThemeSelectionDialog(
            currentTheme = themeState.theme,
            onThemeSelected = { theme ->
                themeViewModel.changeTheme(theme)
                showThemeDialog = false
            },
            onDismiss = { showThemeDialog = false }
        )
    }

    // UI
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Status Bar Placeholder
        Spacer(
            modifier = Modifier
                .height(30.dp)
                .background(MaterialTheme.colorScheme.background)
        )

        // Header Content
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 16.dp, bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back Button
            if (showBackButton) {
                CircularBackButton(
                    onClick = { navController.popBackStack() }
                )
                Spacer(modifier = Modifier.width(16.dp))
            }

            // Title
            Text(
                text = title,
                fontSize = when (screen) {
                    Screen.Home -> 26.sp
                    Screen.NewCategory -> 28.sp
                    is Screen.ViewSubscription, Screen.AddSubscription -> 24.sp
                    else -> 32.sp
                },
                fontWeight = when (screen) {
                    is Screen.ViewSubscription, Screen.AddSubscription -> FontWeight.Medium
                    else -> FontWeight.Bold
                },
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.weight(1f)
            )

            // Home Actions
            if (showHomeActions) {
                HeaderActionButtons(
                    currentTheme = themeState.theme,
                    onThemeClick = { showThemeDialog = true },
                    onNotificationsClick = { /* TODO: gestire notifiche */ }
                )
            }

            // Delete Button
            if (showDeleteButton) {
                DeleteButton(
                    onClick = { showDeleteDialog = true },
                    isCircular = screen is Screen.ViewSubscription
                )
            }
        }
    }
}


/**
 * Bottone indietro circolare
 */
@Composable
private fun CircularBackButton(
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(40.dp)
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(20.dp))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(20.dp))
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "Indietro",
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(20.dp)
        )
    }
}