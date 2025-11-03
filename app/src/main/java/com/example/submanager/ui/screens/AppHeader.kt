package com.example.submanager.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import androidx.navigation.NavController
import com.example.submanager.Screen
import com.example.submanager.viewModel.SubViewModel

/**
 * Header unificato dell'app completamente autonomo.
 * Gestisce automaticamente tutte le azioni (navigazione, tema, delete, ecc.)
 */
@Composable
fun AppHeader(
    screen: Screen?,
    viewModel: SubViewModel,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    // State per il dialog di conferma eliminazione
    var showDeleteDialog by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<String?>(null) }
    val isDarkTheme = viewModel.isDark.value

    // Determina il titolo in base alla schermata
    val title = when (screen) {
        Screen.Home -> "I Miei Abbonamenti"
        Screen.AddSubscription -> "Nuovo Abbonamento"
        Screen.Categories -> "Categorie"
        Screen.NewCategory -> "Nuova Categoria"
        Screen.Insights -> "Statistiche"
        is Screen.ViewSubscription -> {
            if (viewModel.isEditingState.value)
                "Modifica Abbonamento"
            else
                "Dettaglio"
        }
        is Screen.CategoryDetail -> screen.categoryName
        else -> return // nessun header
    }

    // Determina se mostrare il back button
    val showBackButton = screen != Screen.Home

    // Determina se mostrare le azioni Home (tema e notifiche)
    val showHomeActions = screen == Screen.Home

    // Determina se mostrare il bottone delete
    val showDeleteButton = when (screen) {
        is Screen.CategoryDetail -> true
        is Screen.ViewSubscription -> !viewModel.isEditingState.value
        else -> false
    }

    // Dialog di conferma eliminazione
    if (showDeleteDialog && itemToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
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
                        when (screen) {
                            is Screen.CategoryDetail -> {
                                viewModel.deleteCategory(itemToDelete!!)
                            }
                            is Screen.ViewSubscription -> {
                                viewModel.deleteSubscription(screen.subscriptionId)
                            }
                            else -> {}
                        }
                        showDeleteDialog = false
                        navController.popBackStack()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color(0xFFFF6B6B)
                    )
                ) {
                    Text("Elimina")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Annulla")
                }
            }
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
                .padding(
                    top = 16.dp,
                    bottom = 24.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back Button
            if (showBackButton) {
                CircularBackButton(
                    onClick = {
                        // Reset editing mode se necessario
                        if (screen is Screen.ViewSubscription && viewModel.isEditingState.value) {
                            viewModel.resetEditingMode()
                        }
                        navController.popBackStack()
                    }
                )
                Spacer(modifier = Modifier.width(16.dp))
            }

            Text(
                text = title,
                fontSize = when (screen) {
                    Screen.Home -> 26.sp
                    Screen.NewCategory -> 28.sp
                    is Screen.ViewSubscription,
                    Screen.AddSubscription -> 24.sp
                    else -> 32.sp
                },
                fontWeight = when (screen) {
                    is Screen.ViewSubscription,
                    Screen.AddSubscription -> FontWeight.Medium
                    else -> FontWeight.Bold
                },
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.weight(1f)
            )

            // Home Actions (theme + notifications)
            if (showHomeActions) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)){
                    IconButton(
                        onClick = { viewModel.toggleDarkMode() },
                        modifier = Modifier
                            .size(30.dp)
                            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(30.dp))
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(30.dp))
                    ) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = if (isDarkTheme) "Tema chiaro" else "Tema scuro",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(18.dp)
                        )
                    }

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
                    onClick = {
                        itemToDelete = when (screen) {
                            is Screen.CategoryDetail -> screen.categoryName
                            is Screen.ViewSubscription -> screen.subscriptionId.toString()
                            else -> null
                        }
                        showDeleteDialog = true
                    },
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
 * Bottone indietro circolare con bordo
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