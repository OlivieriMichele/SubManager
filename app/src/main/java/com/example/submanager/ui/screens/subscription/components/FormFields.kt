package com.example.submanager.ui.screens.subscription.components

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriceAndDateFields(
    price: String,
    onPriceChange: (String) -> Unit,
    renewalDate: LocalDate?,
    onRenewalDateChange: (LocalDate) -> Unit,
    enabled: Boolean
) {
    var showDatePicker by remember { mutableStateOf(false) }

    // Formatta la data come "20 Ott"
    val dateFormatter = DateTimeFormatter.ofPattern("dd MMM", Locale.ITALIAN)
    val displayDate = renewalDate?.format(dateFormatter) ?: ""

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Campo Prezzo
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Prezzo (€)",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = price,
                onValueChange = onPriceChange,
                enabled = enabled,
                placeholder = {
                    Text("12,99", color = MaterialTheme.colorScheme.onSurfaceVariant)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                    disabledContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    disabledBorderColor = Color.Transparent,
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                    disabledTextColor = MaterialTheme.colorScheme.onBackground
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Campo Data
        Column(modifier = Modifier.weight(1f)) {
            val interactionSource = remember { MutableInteractionSource() }
            Text(
                text = "Rinnovo",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = displayDate,
                onValueChange = { },
                readOnly = true,
                enabled = enabled,
                interactionSource = interactionSource,
                placeholder = {
                    Text("20 Ott", color = MaterialTheme.colorScheme.onSurfaceVariant)
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "Seleziona data",
                        tint = if (enabled)
                            MaterialTheme.colorScheme.onSurfaceVariant
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(20.dp)
                    )
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                    disabledContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    disabledBorderColor = Color.Transparent,
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                    disabledTextColor = MaterialTheme.colorScheme.onBackground
                ),
                modifier = Modifier.fillMaxWidth()
            )

            LaunchedEffect(interactionSource) {
                interactionSource.interactions.collect { interaction ->
                    if(interaction is PressInteraction) {
                        showDatePicker = true
                    }
                }
            }
        }
    }

    // DatePicker Dialog
    if (showDatePicker) {
        DatePickerModal(
            initialDate = renewalDate,
            onDateSelected = { date ->
                onRenewalDateChange(date)
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerModal(
    initialDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val initialMillis = initialDate?.atStartOfDay(java.time.ZoneId.systemDefault())
        ?.toInstant()
        ?.toEpochMilli()
        ?: System.currentTimeMillis()

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialMillis
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = java.time.Instant.ofEpochMilli(millis)
                            .atZone(java.time.ZoneId.systemDefault())
                            .toLocalDate()
                        onDateSelected(date)
                    }
                }
            ) {
                Text("Conferma", color = MaterialTheme.colorScheme.primary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annulla", color = MaterialTheme.colorScheme.onSurface)
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

// ServiceNameField e CategoryField rimangono invariati...
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceNameField(
    serviceName: String,
    onServiceNameChange: (String) -> Unit,
    enabled: Boolean
) {
    Column {
        Text(
            text = "Nome Servizio",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = serviceName,
            onValueChange = onServiceNameChange,
            enabled = enabled,
            placeholder = {
                Text("Es. Netflix", color = MaterialTheme.colorScheme.onSurfaceVariant)
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                disabledContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                disabledBorderColor = Color.Transparent,
                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                disabledTextColor = MaterialTheme.colorScheme.onBackground
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryField(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    categories: List<String>,
    showDropdown: Boolean,
    onDropdownToggle: (Boolean) -> Unit,
    enabled: Boolean
) {
    Column {
        Text(
            text = "Categoria",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        ExposedDropdownMenuBox(
            expanded = showDropdown && enabled,
            onExpandedChange = { if (enabled) onDropdownToggle(it) }
        ) {
            OutlinedTextField(
                value = selectedCategory.ifEmpty { "Seleziona categoria" },
                onValueChange = {},
                readOnly = true,
                enabled = enabled,
                trailingIcon = {
                    if (enabled) {
                        Icon(
                            if (showDropdown) Icons.Default.KeyboardArrowUp
                            else Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                    disabledContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    disabledBorderColor = Color.Transparent,
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledTextColor = MaterialTheme.colorScheme.onBackground
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = showDropdown,
                onDismissRequest = { onDropdownToggle(false) },
                modifier = Modifier.background(MaterialTheme.colorScheme.surface)
            ) {
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                category,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        },
                        onClick = {
                            onCategorySelected(category)
                            onDropdownToggle(false)
                        },
                        colors = MenuDefaults.itemColors(
                            textColor = MaterialTheme.colorScheme.onBackground
                        )
                    )
                }
            }
        }
    }
}