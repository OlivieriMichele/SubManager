package com.example.submanager.ui.screens.subscription

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.submanager.model.Subscription
import com.example.submanager.ui.screens.subscription.components.CategoryField
import com.example.submanager.ui.screens.subscription.components.ColorPicker
import com.example.submanager.ui.screens.subscription.components.FormTopBar
import com.example.submanager.ui.screens.subscription.components.IconPreview
import com.example.submanager.ui.screens.subscription.components.PriceAndDateFields
import com.example.submanager.ui.screens.subscription.components.ServiceNameField
import com.example.submanager.ui.screens.subscription.components.SubscriptionColors
import com.example.submanager.viewModel.SubViewModel
import java.time.LocalDate

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
    var renewalDate by remember { mutableStateOf(subscription?.nextBilling ?: LocalDate.now()) }
    var serviceName by remember { mutableStateOf(subscription?.name ?: "") }
    var selectedCategory by remember { mutableStateOf(subscription?.category ?: "") }
    var selectedColor by remember {
        mutableStateOf(subscription?.color ?: SubscriptionColors.availableColors.first())
    }
    var showCategoryDropdown by remember { mutableStateOf(false) }

    val isEditing = when (mode) {
        FormMode.VIEW -> viewModel.isEditingState.value
        else -> true
    }

    val saveTrigger by viewModel.saveTrigger

    LaunchedEffect(saveTrigger) {
        if (saveTrigger > 0 && isEditing) {
            val newSubscription = Subscription(
                id = subscription?.id ?: (0..10000).random(),
                name = serviceName,
                price = price.toDoubleOrNull() ?: 0.0,
                color = selectedColor,
                nextBilling = renewalDate,
                category = selectedCategory
            )
            onSave(newSubscription)

            // RESET del trigger dopo il salvataggio
            viewModel.resetSaveTrigger()

            // Dopo il salvataggio, esci dalla modalità editing
            if (mode == FormMode.VIEW) {
                viewModel.resetEditingMode()
            }
        }
    }

    DisposableEffect(Unit) {
        // Reset del trigger quando esci dalla schermata
        onDispose {
            viewModel.resetSaveTrigger()
        }
    }

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