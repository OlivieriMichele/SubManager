package com.example.submanager.ui.screens.subscription

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.submanager.model.Subscription
import com.example.submanager.ui.screens.subscription.components.*
import com.example.submanager.viewModel.SubViewModel
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember

enum class FormMode {
    CREATE,
    EDIT,
    VIEW
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionFormScreen(
    viewModel: SubViewModel,
    mode: FormMode,
    subscription: Subscription? = null,
    onNavigateBack: () -> Unit,
    onSave: (Subscription) -> Unit = {},
    onDelete: () -> Unit = {}
) {
    // Form States
    var price by remember { mutableStateOf(subscription?.price?.toString() ?: "") }
    var renewalDate by remember { mutableStateOf(subscription?.nextBilling ?: "") }
    var serviceName by remember { mutableStateOf(subscription?.name ?: "") }
    var selectedCategory by remember { mutableStateOf(subscription?.category ?: "") }
    var selectedColor by remember {
        mutableStateOf(subscription?.color ?: SubscriptionColors.availableColors.first())
    }
    var showCategoryDropdown by remember { mutableStateOf(false) }
    var isEditing by remember { mutableStateOf(mode != FormMode.VIEW) }

    // To-Do: questo è solo per i test, implementa una funzione che prenda le vere categories
    val categories = listOf("Intrattenimento", "Software", "Fitness", "Shopping")

    val screenTitle = when (mode) {
        FormMode.CREATE -> "Nuovo Abbonamento"
        FormMode.EDIT -> "Modifica Abbonamento"
        FormMode.VIEW -> "Dettaglio"
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            FormTopBar(
                title = screenTitle,
                showDeleteButton = mode != FormMode.CREATE,
                onNavigateBack = onNavigateBack,
                onDelete = onDelete
            )
        },
        floatingActionButton = {
            if (mode == FormMode.VIEW && !isEditing) {
                FloatingActionButton(
                    onClick = { isEditing = true },
                    shape = CircleShape,
                    containerColor = Color.Transparent,
                    modifier = Modifier
                        .size(56.dp)
                        .background(MaterialTheme.colorScheme.primary, shape = CircleShape)
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Modifica",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            } else if (isEditing) {
                FloatingActionButton(
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
                    shape = CircleShape,
                    containerColor = Color.Transparent,
                    modifier = Modifier
                        .size(56.dp)
                        .background(MaterialTheme.colorScheme.primary, shape = CircleShape)
                ) {
                    Icon(
                        Icons.Default.Check,
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

            IconPreview(
                serviceName = serviceName,
                color = selectedColor,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(32.dp))

            PriceAndDateFields(
                price = price,
                onPriceChange = { if (isEditing) price = it },
                renewalDate = renewalDate,
                onRenewalDateChange = { if (isEditing) renewalDate = it },
                enabled = isEditing
            )

            Spacer(modifier = Modifier.height(24.dp))

            ServiceNameField(
                serviceName = serviceName,
                onServiceNameChange = { if (isEditing) serviceName = it },
                enabled = isEditing
            )

            Spacer(modifier = Modifier.height(24.dp))

            CategoryField(
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it },
                categories = categories,
                showDropdown = showCategoryDropdown,
                onDropdownToggle = { showCategoryDropdown = it },
                enabled = isEditing
            )

            Spacer(modifier = Modifier.height(24.dp))

            ColorPicker(
                selectedColor = selectedColor,
                availableColors = SubscriptionColors.availableColors,
                onColorSelected = { selectedColor = it },
                enabled = isEditing
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun IconPreview(
    serviceName: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(100.dp)
            .background(color, RoundedCornerShape(24.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (serviceName.isNotEmpty()) serviceName.first().uppercase() else "+",
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
fun ColorPicker(
    selectedColor: Color,
    availableColors: List<Color>,
    onColorSelected: (Color) -> Unit,
    enabled: Boolean = true
) {
    Column {
        Text(
            text = "Colore",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground,
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
                                MaterialTheme.colorScheme.onBackground
                            } else Color.Transparent,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable(
                            enabled = enabled,
                            onClick = { onColorSelected(color) },
                            indication = null, // QUESTO RISOLVE IL PROBLEMA
                            interactionSource = remember { MutableInteractionSource() }
                        )
                )
            }
        }
    }
}

// Colori predefiniti
object SubscriptionColors {
    val availableColors = listOf(
        Color(0xFFC4B5FD), // Pastel Purple 1
        Color(0xFFBAE6FD), // Pastel Blue 1
        Color(0xFFA78BFA), // Pastel Purple 2
        Color(0xFF93C5FD), // Pastel Blue 2
        Color(0xFFFBCFE8), // Pink
        Color(0xFF99F6E4)  // Turquoise
    )
}