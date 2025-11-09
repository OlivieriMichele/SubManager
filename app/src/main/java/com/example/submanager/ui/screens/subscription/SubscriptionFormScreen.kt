package com.example.submanager.ui.screens.subscription

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.submanager.ui.screens.subscription.components.CategoryField
import com.example.submanager.ui.screens.subscription.components.ColorPicker
import com.example.submanager.ui.screens.subscription.components.IconPreview
import com.example.submanager.ui.screens.subscription.components.PriceAndDateFields
import com.example.submanager.ui.screens.subscription.components.ServiceNameField
import com.example.submanager.ui.screens.subscription.components.SubscriptionColors

@Composable
fun SubscriptionFormScreen(
    state: SubscriptionFormState,
    actions: SubscriptionActions
) {
    var showCategoryDropdown by remember { mutableStateOf(false) }

    /* Debug: Log dello state ogni volta che cambia
    LaunchedEffect(state) {
        Log.d("SubscriptionForm", """
            State:
            - serviceName: '${state.serviceName}'
            - price: '${state.price}'
            - category: '${state.selectedCategory}'
            - validationError: ${state.validationError}
            - isSaving: ${state.isSaving}
            - availableCategories: ${state.availableCategories}
        """.trimIndent())
    }*/

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

            Spacer(modifier = Modifier.height(24.dp))

            // ========== VALIDATION ERROR (SEMPRE VISIBILE SE PRESENTE) ==========
            if (state.validationError != null) {
                Text(
                    text = "${state.validationError}", //⚠️
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.errorContainer,
                            RoundedCornerShape(12.dp)
                        )
                        .padding(16.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // ========== SAVING INDICATOR ==========
            if (state.isSaving) {
                Text(
                    text = "Salvataggio in corso...", //💾
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.primaryContainer,
                            RoundedCornerShape(12.dp)
                        )
                        .padding(16.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}