package com.example.submanager.ui.composable.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.submanager.ui.Screen
import com.example.submanager.ui.screens.viewModel.CategoryViewModel
import com.example.submanager.ui.screens.viewModel.SubscriptionViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun DeleteConfirmationDialog(
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
                    when (screen) {
                        is Screen.CategoryDetail -> {
                            categoryViewModel.actions.deleteCategory(screen.categoryName) {
                                navController.popBackStack()
                            }
                        }
                        is Screen.ViewSubscription -> {
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

@Composable
fun DeleteButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    isCircular: Boolean = false
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(40.dp)
            .background(
                Color(0xFFFF6B6B).copy(alpha = 0.15f),
                if (isCircular) CircleShape else RoundedCornerShape(20.dp)
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