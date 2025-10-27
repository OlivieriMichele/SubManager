package com.example.submanager.ui.screens.subscription.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

enum class FabType {
    ADD,
    EDIT,
    SAVE,
    NONE
}

@Composable
fun AppFloatingActionButton(
    fabType: FabType,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (fabType == FabType.NONE) return

    FloatingActionButton(
        onClick = onClick,
        shape = CircleShape,
        modifier = modifier
            .size(56.dp)
            .background(MaterialTheme.colorScheme.primary, shape = CircleShape)
    ) {
        when (fabType) {
            FabType.ADD -> {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Crea",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            FabType.EDIT -> {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Modifica",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            FabType.SAVE -> {
                Icon(
                    Icons.Default.Check,
                    contentDescription = "Salva",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
            FabType.NONE -> { /* Non mostrare nulla */ }
        }
    }
}