package com.example.submanager.ui.screens.subscription

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.example.submanager.ui.screens.subscription.components.IconPreview
import com.example.submanager.ui.screens.subscription.components.PriceAndDateFields
import com.example.submanager.ui.screens.subscription.components.ServiceNameField
import com.example.submanager.ui.screens.subscription.components.SubscriptionColors
import com.example.submanager.viewModel.SubViewModel
import java.time.LocalDate

@Composable
fun SubscriptionFormScreen(
    state: SubscriptionFormState,
    actions: SubscriptionActions
) {
    var showCategoryDropdown by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
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
                serviceName = state.serviceName,
                color = state.selectedColor,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(32.dp))

            PriceAndDateFields(
                price = state.price,
                onPriceChange = { actions.setPrice(it) },
                renewalDate = state.renewalDate,
                onRenewalDateChange = { actions.setRenewalDate(it) },
                enabled = state.isEditing
            )

            Spacer(modifier = Modifier.height(24.dp))

            ServiceNameField(
                serviceName = state.serviceName,
                onServiceNameChange = { actions.setServiceName(it) },
                enabled = state.isEditing
            )

            Spacer(modifier = Modifier.height(24.dp))

            CategoryField(
                selectedCategory = state.selectedCategory,
                onCategorySelected = { actions.setCategory(it) },
                categories = state.availableCategories,
                showDropdown = showCategoryDropdown,
                onDropdownToggle = { showCategoryDropdown = it },
                enabled = state.isEditing
            )

            Spacer(modifier = Modifier.height(24.dp))

            ColorPicker(
                selectedColor = state.selectedColor,
                availableColors = SubscriptionColors.availableColors,
                onColorSelected = { actions.setColor(it) },
                enabled = state.isEditing
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}