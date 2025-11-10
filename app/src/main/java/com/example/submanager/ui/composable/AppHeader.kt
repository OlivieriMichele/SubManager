package com.example.submanager.ui.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.submanager.ui.Screen
import com.example.submanager.ui.screens.viewModel.CategoryViewModel
import com.example.submanager.ui.screens.viewModel.SubscriptionViewModel
import com.example.submanager.ui.screens.viewModel.ThemeViewModel
import org.koin.androidx.compose.koinViewModel
import com.example.submanager.data.models.Theme

@Composable
fun AppHeader(
    screen: Screen?,
    navController: NavController,
    themeViewModel: ThemeViewModel,
    modifier: Modifier = Modifier
) {
    // State per il dialog di conferma eliminazione
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }

    val themeState by themeViewModel.state.collectAsStateWithLifecycle()

    // Determina il titolo in base alla schermata
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

    // Determina se mostrare i vari elementi
    val showBackButton = screen != Screen.Home
    val showHomeActions = screen == Screen.Home
    val showDeleteButton = screen is Screen.CategoryDetail || screen is Screen.ViewSubscription

    // ========== DIALOG DI CONFERMA DELETE ==========
    if (showDeleteDialog) {
        DeleteConfirmationDialog(
            screen = screen,
            navController = navController,
            onDismiss = { showDeleteDialog = false }
        )
    }

    // ========== DIALOG SELEZIONE TEMA ==========
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

            // Home Actions (theme + notifications)
            if (showHomeActions) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Theme Toggle
                    IconButton(
                        onClick = { showThemeDialog = true },
                        modifier = Modifier
                            .size(30.dp)
                            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(30.dp))
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(30.dp))
                    ) {
                        Icon(
                            imageVector = when(themeState.theme) {
                                Theme.Light -> Icons.Default.LightMode
                                Theme.Dark -> Icons.Default.DarkMode
                                Theme.System -> Icons.Default.Contrast
                            },
                            contentDescription = "Cambia tema",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Notifications
                    IconButton(
                        onClick = { /* TODO: gestire notifiche */ },
                        modifier = Modifier
                            .size(30.dp)
                            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(30.dp))
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(30.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifiche",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Delete Button
            if (showDeleteButton) {
                IconButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            Color(0xFFFF6B6B).copy(alpha = 0.15f),
                            if (screen is Screen.ViewSubscription) CircleShape else RoundedCornerShape(20.dp)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Elimina",
                        tint = Color(0xFFFF6B6B),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

/**
 * Dialog di conferma eliminazione - AUTONOMO
 * Ottiene il ViewModel necessario e gestisce la delete internamente
 */
@Composable
private fun DeleteConfirmationDialog(
    screen: Screen?,
    navController: NavController,
    onDismiss: () -> Unit,
    subscriptionViewModel: SubscriptionViewModel = koinViewModel(),
    categoryViewModel: CategoryViewModel = koinViewModel()
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (screen is Screen.CategoryDetail) "Elimina Categoria"
                else "Elimina Abbonamento"
            )
        },
        text = {
            Text(
                if (screen is Screen.CategoryDetail)
                    "Sei sicuro di voler eliminare questa categoria? Verranno eliminati anche tutti gli abbonamenti associati."
                else
                    "Sei sicuro di voler eliminare questo abbonamento?"
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    // ========== LOGICA DELETE AUTONOMA ==========
                    when (screen) {
                        is Screen.CategoryDetail -> {
                            // Ottiene CategoryViewModel e chiama delete
                            categoryViewModel.actions.deleteCategory(screen.categoryName) {
                                navController.popBackStack()
                            }
                        }
                        is Screen.ViewSubscription -> {
                            // Ottiene SubscriptionViewModel e chiama delete
                            subscriptionViewModel.actions.deleteSubscription(screen.subscriptionId) {
                                navController.popBackStack()
                            }
                        }
                        else -> {}
                    }
                    onDismiss()
                },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color(0xFFFF6B6B)
                )
            ) {
                Text("Elimina")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annulla")
            }
        }
    )
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

/**
 * Dialog per la selezione del tema
 */
@Composable
private fun ThemeSelectionDialog(
    currentTheme: Theme,
    onThemeSelected: (Theme) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Scegli Tema",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column(
                modifier = Modifier.selectableGroup()
            ) {
                Theme.entries.forEach { theme ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (theme == currentTheme),
                            onClick = { onThemeSelected(theme) }
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Icon(
                            imageVector = when (theme) {
                                Theme.Light -> Icons.Default.LightMode
                                Theme.Dark -> Icons.Default.DarkMode
                                Theme.System -> Icons.Default.Contrast
                            },
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = when (theme) {
                                    Theme.Light -> "Chiaro"
                                    Theme.Dark -> "Scuro"
                                    Theme.System -> "Sistema"
                                },
                                style = MaterialTheme.typography.bodyLarge
                            )
                            if (theme == Theme.System) {
                                Text(
                                    text = "Segue le impostazioni del dispositivo",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Chiudi")
            }
        }
    )
}