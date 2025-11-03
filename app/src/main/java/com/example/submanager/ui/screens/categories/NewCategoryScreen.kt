package com.example.submanager.ui.screens.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.submanager.viewModel.SubViewModel

@Composable
fun NewCategoryScreen(
    viewModel: SubViewModel,
    onCategorySaved: () -> Unit = {}
) {
    // Todo: scomponi in elementi separati per argomenti
    var budget by remember { mutableStateOf("") }
    var categoryName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedIconIndex by remember { mutableIntStateOf(0) }
    var selectedGradientIndex by remember { mutableIntStateOf(0) }

    val availableIcons = listOf(
        Icons.Default.ShoppingCart,
        Icons.Default.Build,
        Icons.Default.Favorite,
        Icons.Default.Email,
        Icons.Default.DateRange,
        Icons.Default.Face,
        Icons.Default.Home
    )

    // To-Do: aggiorna per rendere più icone selezionabili
    val gradientOptions = listOf(
        listOf(Color(0xFFB8B5FF), Color(0xFFDED9FF)),
        listOf(Color(0xFFD9B5FF), Color(0xFFEFDEFF)),
        listOf(Color(0xFFB5D9FF), Color(0xFFDEF0FF))
    )

    val saveCategoryTrigger by viewModel.saveCategoryTrigger

    LaunchedEffect(saveCategoryTrigger) {
        if (saveCategoryTrigger > 0) {
            val budgetValue = budget.toDoubleOrNull() ?: 0.0

            viewModel.addCategory(
                name = categoryName,
                budget = budgetValue,
                description = description,
                icon = availableIcons[selectedIconIndex],
                gradientIndex = selectedGradientIndex
            )

            // RESET del trigger dopo il salvataggio
            viewModel.resetSaveCategoryTrigger()

            // Callback per navigare indietro
            onCategorySaved()
        }
    }

    DisposableEffect(Unit) {
        // Reset del trigger quando esci dalla schermata
        onDispose {
            viewModel.resetSaveCategoryTrigger()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icon Preview To-Do: aggiorna rendendolo un oggetto a parte
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        brush = Brush.linearGradient(gradientOptions[selectedGradientIndex]),
                        shape = RoundedCornerShape(20.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = availableIcons[selectedIconIndex],
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Nome Categoria To-Do: aggiorna rendendolo un abstract-object per riusarlo con budget e descrizione
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Nome Categoria",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = categoryName,
                    onValueChange = { categoryName = it },
                    placeholder = {
                        Text("Es. Intrattenimento", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedBorderColor = MaterialTheme.colorScheme.outline,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Budget Mensile
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Budget Mensile (€)",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = budget,
                    onValueChange = { budget = it },
                    placeholder = {
                        Text("50,00", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedBorderColor = MaterialTheme.colorScheme.outline,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Imposta un limite di spesa per questa categoria",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Descrizione
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Descrizione (opzionale)",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = {
                        Text("Breve descrizione della categoria", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    },
                    minLines = 3,
                    maxLines = 5,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedBorderColor = MaterialTheme.colorScheme.outline,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Icon Selector To-Do: aggiorna rendendolo un oggetto a parte
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Icona",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    availableIcons.forEachIndexed { index, icon ->
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    color = if (selectedIconIndex == index)
                                        MaterialTheme.colorScheme.primaryContainer
                                    else
                                        MaterialTheme.colorScheme.surface,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (selectedIconIndex == index)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.outlineVariant,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) { selectedIconIndex = index },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = if (selectedIconIndex == index)
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                else
                                    MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Gradiente
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Gradiente",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    gradientOptions.forEachIndexed { index, gradient ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(60.dp)
                                .background(
                                    brush = Brush.linearGradient(gradient),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .border(
                                    width = if (selectedGradientIndex == index) 3.dp else 0.dp,
                                    color = if (selectedGradientIndex == index) Color.White else Color.Transparent,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) { selectedGradientIndex = index },
                            contentAlignment = Alignment.Center
                        ) {
                            if (selectedGradientIndex == index) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(120.dp)) // Spazio per Bottom Nav
        }
    }
}