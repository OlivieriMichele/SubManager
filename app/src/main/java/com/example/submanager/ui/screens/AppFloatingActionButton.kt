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
import androidx.navigation.NavController
import com.example.submanager.Screen
import com.example.submanager.viewModel.SubViewModel

@Composable
fun AppFloatingActionButton(
    modifier: Modifier = Modifier,
    screen: Screen?,
    navController: NavController,
    viewModel: SubViewModel
){
    fun setEdit(): () -> Unit = { viewModel.setEditingMode(true) }
    fun save(): () -> Unit = { viewModel.triggerSave()}
    fun saveCategory(): () -> Unit = { viewModel.triggerSaveCategory()}
    fun addCategory(): () -> Unit = { navController.navigate(Screen.NewCategory) }
    fun addSubscription(): () -> Unit = { navController.navigate(Screen.AddSubscription) }

    // Determine icon and action in order to currentScreen
    val (icon, description, action) = when (screen) {
        Screen.Home -> Triple(Icons.Default.Add, "Aggiungi", addSubscription())

        Screen.AddSubscription -> Triple(Icons.Default.Check, "Salva", save())

        Screen.Categories -> Triple(Icons.Default.Add, "Aggiungi", addCategory())

        Screen.NewCategory -> Triple(Icons.Default.Save, "Salva Categoria", saveCategory())

        is Screen.ViewSubscription -> {
            if (viewModel.isEditingState.value)
                Triple(Icons.Default.Check, "Salva Abbonamento", save())
            else
                Triple(Icons.Default.Edit, "Modifica", setEdit())
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