// ============================================
// SUBSCRIPTION FORM - COMPONENTE RIUSABILE
// File: ui/screens/SubscriptionFormScreen.kt
// ============================================

package com.example.submanager.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.submanager.model.Subscription
import com.example.submanager.ui.theme.AccentColors
import com.example.submanager.viewModel.SubViewModel

// (In un'applicazione reale, AccentColors dovrebbe essere definito nel tuo pacchetto ui.theme)


// Enum per distinguere la modalità
enum class FormMode {
    CREATE,  // Creazione nuovo abbonamento
    EDIT,    // Modifica abbonamento esistente
    VIEW     // Solo visualizzazione (dettaglio)
}

// ============================================
// SCHERMATA RIUSABILE
// ============================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionFormScreen(
    viewModel: SubViewModel,
    mode: FormMode,
    subscription: Subscription? = null, // null se mode = CREATE
    onNavigateBack: () -> Unit,
    onSave: (Subscription) -> Unit = {},
    onDelete: () -> Unit = {}
) {
    val colorScheme = MaterialTheme.colorScheme

    // Form States - Inizializzati con i dati esistenti se in modalità EDIT/VIEW
    var price by remember { mutableStateOf(subscription?.price?.toString() ?: "") }
    var renewalDate by remember { mutableStateOf(subscription?.nextBilling ?: "") }
    var serviceName by remember { mutableStateOf(subscription?.name ?: "") }
    var selectedCategory by remember { mutableStateOf(subscription?.category ?: "") }
    var selectedColor by remember { mutableStateOf(subscription?.color ?: AccentColors.pastelPurple) }
    var showCategoryDropdown by remember { mutableStateOf(false) }

    // Stato per abilitare/disabilitare l'editing in modalità VIEW
    var isEditing by remember { mutableStateOf(mode != FormMode.VIEW) }

    val availableColors = listOf(
        AccentColors.pastelPurple,
        AccentColors.pastelBlue,
        AccentColors.pastelPurple,
        AccentColors.pastelBlue,
        Color(0xFFFBCFE8),
        Color(0xFF99F6E4)
    )

    val categories = listOf("Intrattenimento", "Software", "Fitness", "Shopping")

    // Titolo dinamico
    val screenTitle = when (mode) {
        FormMode.CREATE -> "Nuovo Abbonamento"
        FormMode.EDIT -> "Modifica Abbonamento"
        FormMode.VIEW -> "Dettaglio"
    }

    Scaffold(
        containerColor = colorScheme.background,
        topBar = {
            // Custom TopBar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colorScheme.background)
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                colorScheme.surface,
                                CircleShape
                            )
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Indietro",
                            tint = colorScheme.onBackground,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Text(
                        text = screenTitle,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = colorScheme.onBackground
                    )
                }

                // Bottone Elimina (solo in modalità VIEW/EDIT)
                if (mode != FormMode.CREATE) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                Color(0xFFEF4444).copy(alpha = 0.2f),
                                CircleShape
                            )
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Elimina",
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            // FAB diverso in base alla modalità
            if (mode == FormMode.VIEW) {
                // In modalità VIEW mostra pulsante "Modifica"
                FloatingActionButton(
                    onClick = { isEditing = true },
                    shape = CircleShape,
                    containerColor = Color.Transparent,
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.linearGradient(
                                colors = listOf(
                                    AccentColors.darkBlue.copy(alpha = 0.8f),
                                    AccentColors.darkPurple.copy(alpha = 0.8f)
                                )
                            ),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Modifica",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            } else {
                // In modalità CREATE/EDIT mostra pulsante "Salva"
                FloatingActionButton(
                    onClick = {
                        // TODO: Validazione
                        val newSubscription = Subscription(
                            id = subscription?.id ?: (0..10000).random(),
                            name = serviceName,
                            price = price.toDoubleOrNull() ?: 0.0,
                            color = selectedColor,
                            nextBilling = renewalDate,
                            category = selectedCategory
                        )
                        onSave(newSubscription)
                    },
                    shape = CircleShape,
                    containerColor = Color.Transparent,
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.linearGradient(
                                colors = listOf(
                                    AccentColors.darkBlue.copy(alpha = 0.8f),
                                    AccentColors.darkPurple.copy(alpha = 0.8f)
                                )
                            ),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        if (mode == FormMode.CREATE) Icons.Default.Add else Icons.Default.Check,
                        contentDescription = "Salva",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Icon Preview
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.CenterHorizontally)
                    .background(selectedColor, RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (serviceName.isNotEmpty()) serviceName.first().uppercase() else "+",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Price and Renewal Date Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Prezzo
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Prezzo (€)",
                        fontSize = 14.sp,
                        color = colorScheme.onBackground, // Sostituito colors.textPrimary
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = price,
                        onValueChange = { if (isEditing) price = it },
                        enabled = isEditing,
                        placeholder = { Text("12,99", color = colorScheme.onSurfaceVariant) }, // Sostituito colors.textSecondary
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = colorScheme.surface,
                            unfocusedContainerColor = colorScheme.surface,
                            disabledContainerColor = colorScheme.surface,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            disabledBorderColor = Color.Transparent,
                            focusedTextColor = colorScheme.onSurface,
                            unfocusedTextColor = colorScheme.onSurface,
                            disabledTextColor = colorScheme.onSurface
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Rinnovo
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Rinnovo",
                        fontSize = 14.sp,
                        color = colorScheme.onBackground,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = renewalDate,
                        onValueChange = { if (isEditing) renewalDate = it },
                        enabled = isEditing,
                        placeholder = { Text("20 Ott", color = colorScheme.onSurfaceVariant) }, // Sostituito colors.textSecondary
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = colorScheme.surface, // Sostituito colors.surfaceAlpha/colors.surface
                            unfocusedContainerColor = colorScheme.surface, // Sostituito colors.surfaceAlpha/colors.surface
                            disabledContainerColor = colorScheme.surface, // Sostituito colors.surfaceAlpha/colors.surface
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            disabledBorderColor = Color.Transparent,
                            focusedTextColor = colorScheme.onSurface, // Sostituito colors.textPrimary
                            unfocusedTextColor = colorScheme.onSurface, // Sostituito colors.textPrimary
                            disabledTextColor = colorScheme.onSurface // Sostituito colors.textPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Nome Servizio
            Column {
                Text(
                    text = "Nome Servizio",
                    fontSize = 14.sp,
                    color = colorScheme.onBackground, // Sostituito colors.textPrimary
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = serviceName,
                    onValueChange = { if (isEditing) serviceName = it },
                    enabled = isEditing,
                    placeholder = { Text("Es. Netflix", color = colorScheme.onSurfaceVariant) }, // Sostituito colors.textSecondary
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = colorScheme.surface, // Sostituito colors.surfaceAlpha/colors.surface
                        unfocusedContainerColor = colorScheme.surface, // Sostituito colors.surfaceAlpha/colors.surface
                        disabledContainerColor = colorScheme.surface, // Sostituito colors.surfaceAlpha/colors.surface
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        disabledBorderColor = Color.Transparent,
                        focusedTextColor = colorScheme.onSurface, // Sostituito colors.textPrimary
                        unfocusedTextColor = colorScheme.onSurface, // Sostituito colors.textPrimary
                        disabledTextColor = colorScheme.onSurface // Sostituito colors.textPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Categoria
            Column {
                Text(
                    text = "Categoria",
                    fontSize = 14.sp,
                    color = colorScheme.onBackground, // Sostituito colors.textPrimary
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                ExposedDropdownMenuBox(
                    expanded = showCategoryDropdown && isEditing,
                    onExpandedChange = { if (isEditing) showCategoryDropdown = it }
                ) {
                    OutlinedTextField(
                        value = selectedCategory.ifEmpty { "Seleziona categoria" },
                        onValueChange = {},
                        readOnly = true,
                        enabled = isEditing,
                        trailingIcon = {
                            if (isEditing) {
                                Icon(
                                    if (showCategoryDropdown) Icons.Default.KeyboardArrowUp
                                    else Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = colorScheme.onSurfaceVariant // Sostituito colors.textSecondary
                                )
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = colorScheme.surface, // Sostituito colors.surfaceAlpha/colors.surface
                            unfocusedContainerColor = colorScheme.surface, // Sostituito colors.surfaceAlpha/colors.surface
                            disabledContainerColor = colorScheme.surface, // Sostituito colors.surfaceAlpha/colors.surface
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            disabledBorderColor = Color.Transparent,
                            focusedTextColor = colorScheme.onSurface, // Sostituito colors.textPrimary
                            unfocusedTextColor = colorScheme.onSurfaceVariant, // Sostituito colors.textSecondary
                            disabledTextColor = colorScheme.onSurface // Sostituito colors.textPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = showCategoryDropdown,
                        onDismissRequest = { showCategoryDropdown = false },
                        modifier = Modifier.background(
                            colorScheme.surface // Sostituito colors.surface
                        )
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        category,
                                        color = colorScheme.onSurface // Sostituito colors.textPrimary
                                    )
                                },
                                onClick = {
                                    selectedCategory = category
                                    showCategoryDropdown = false
                                },
                                colors = MenuDefaults.itemColors(
                                    textColor = colorScheme.onSurface // Sostituito colors.textPrimary
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Colore
            Column {
                Text(
                    text = "Colore",
                    fontSize = 14.sp,
                    color = colorScheme.onBackground, // Sostituito colors.textPrimary
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(availableColors) { color ->
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(color, RoundedCornerShape(12.dp))
                                .border(
                                    width = 3.dp,
                                    color = if (selectedColor == color) {
                                        // Ho mantenuto un colore esplicito se non c'è un colore standard per il bordo
                                        if (viewModel.isDark.value) Color.White else colorScheme.onBackground
                                    } else Color.Transparent,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable(enabled = isEditing) {
                                    selectedColor = color
                                }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Bottone Salva (solo se in editing mode)
            if (isEditing) {
                Button(
                    onClick = {
                        val newSubscription = Subscription(
                            id = subscription?.id ?: (0..10000).random(),
                            name = serviceName,
                            price = price.toDoubleOrNull() ?: 0.0,
                            color = selectedColor,
                            nextBilling = renewalDate,
                            category = selectedCategory
                        )
                        onSave(newSubscription)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = androidx.compose.ui.graphics.Brush.linearGradient(
                                    colors = listOf(
                                        AccentColors.darkBlue.copy(alpha = 0.8f),
                                        AccentColors.darkPurple.copy(alpha = 0.8f)
                                    )
                                ),
                                shape = RoundedCornerShape(16.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Save,
                                contentDescription = null,
                                tint = Color.White
                            )
                            Text(
                                text = if (mode == FormMode.CREATE) "Crea Abbonamento" else "Salva Modifiche",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}