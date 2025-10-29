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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.submanager.Screen
import com.example.submanager.viewModel.SubViewModel

@Composable
fun AppFloatingActionButton(
    screen: Screen?,
    viewModel: SubViewModel,
    onAdd: () -> Unit,
    onEdit: () -> Unit,
    onSave: () -> Unit,
    onAddCategory: () -> Unit,
    onSaveCategory: () -> Unit,
    modifier: Modifier = Modifier
){
    // Determina icona e azione in base alla schermata
    val (icon, description, action) = when (screen) {
        Screen.Home -> Triple(Icons.Default.Add, "Aggiungi", onAdd)

        Screen.AddSubscription -> Triple(Icons.Default.Check, "Salva", onSave)

        Screen.Categories -> Triple(Icons.Default.Add, "Aggiungi", onAddCategory)

        Screen.NewCategory -> Triple(Icons.Default.Save, "Salva Categoria", onSaveCategory)

        is Screen.ViewSubscription -> {
            if (viewModel.isEditingState.value)
                Triple(Icons.Default.Check, "Salva Abbonamento", onSave)
            else
                Triple(Icons.Default.Edit, "Modifica", onEdit)
        }

        else -> return // nessun FAB
    }

    FloatingActionButton(
        onClick = action,
        shape = CircleShape,
        modifier = modifier
            .size(56.dp)
            .background(MaterialTheme.colorScheme.primary, shape = CircleShape)
    ) {
        Icon(
            icon,
            contentDescription = description,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}