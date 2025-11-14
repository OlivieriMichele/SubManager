package com.example.submanager.ui.screens.categories

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.submanager.ui.screens.viewModel.CategoryActions
import com.example.submanager.ui.screens.viewModel.CategoryFormState
import com.example.submanager.ui.theme.CategoryIcons
import com.example.submanager.ui.screens.categories.components.CategoryIconPreview
import com.example.submanager.ui.screens.categories.components.CategoryInputField
import com.example.submanager.ui.screens.categories.components.GradientSelector
import com.example.submanager.ui.screens.categories.components.IconSelector

@Composable
fun NewCategoryScreen(
    state: CategoryFormState,
    actions: CategoryActions
) {
    val availableIcons = CategoryIcons.availableIcons
    val gradientOptions = CategoryIcons.gradientOptions

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
            // Icon Preview
            CategoryIconPreview(
                icon = availableIcons[state.selectedIconIndex],
                gradient = gradientOptions[state.selectedGradientIndex]
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Nome Categoria
            CategoryInputField(
                label = "Nome Categoria",
                value = state.name,
                onValueChange = { actions.setName(it) },
                placeholder = "Es. Intrattenimento",
                singleLine = true
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Budget Mensile
            CategoryInputField(
                label = "Budget Mensile (€)",
                value = state.budget,
                onValueChange = { actions.setBudget(it) },
                placeholder = "50,00",
                singleLine = true,
                helperText = "Imposta un limite di spesa per questa categoria"
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Descrizione
            CategoryInputField(
                label = "Descrizione (opzionale)",
                value = state.description,
                onValueChange = { actions.setDescription(it) },
                placeholder = "Breve descrizione della categoria",
                singleLine = false,
                minLines = 3,
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Icon Selector
            IconSelector(
                availableIcons = availableIcons,
                selectedIconIndex = state.selectedIconIndex,
                onIconSelected = { actions.setIconIndex(it) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Gradiente Selector
            GradientSelector(
                gradientOptions = gradientOptions,
                selectedGradientIndex = state.selectedGradientIndex,
                onGradientSelected = { actions.setGradientIndex(it) }
            )

            // Validation Error
            if (state.validationError != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = state.validationError,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.errorContainer,
                            RoundedCornerShape(8.dp)
                        )
                        .padding(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(120.dp))
        }
    }
}