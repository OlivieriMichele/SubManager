package com.example.submanager.ui.screens.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.submanager.ui.screens.categories.components.CategoryBudgetCard
import com.example.submanager.ui.screens.categories.components.CategoryDescriptionCard
import com.example.submanager.ui.screens.categories.components.CategoryStatisticsCard
import com.example.submanager.ui.screens.categories.components.CategorySubscriptionItem
import com.example.submanager.ui.screens.viewModel.CategoryDetailState

@Composable
fun CategoryDetailScreen(
    state: CategoryDetailState,
    onSubscriptionClick: (Int) -> Unit = {}
) {
    val categoryData = state.category
    val categorySubs = state.subscriptions

    if (categoryData == null) {
        // TODO: Gestione errore o caricamento
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Content
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            item {
                // Budget Card (al posto di CategoryDetailCard)
                CategoryBudgetCard(categoryData)
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                // Description Card
                CategoryDescriptionCard(categoryData.description)
                if (categoryData.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            item {
                // Statistics Card
                val averagePrice = if (categorySubs.isNotEmpty())
                    categoryData.total / categorySubs.size
                else
                    0.0

                CategoryStatisticsCard(
                    activeSubscriptions = categorySubs.size,
                    yearlySpending = categoryData.total * 12,
                    averagePerService = averagePrice
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                // Abbonamenti Title
                Text(
                    text = "Abbonamenti (${categorySubs.size})",
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // Subscriptions List
            if (categorySubs.isNotEmpty()) {
                items(categorySubs) { sub ->
                    CategorySubscriptionItem(subscription = sub)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            } else {
                item {
                    EmptyCategoryState()
                }
            }

            item {
                Spacer(modifier = Modifier.height(120.dp)) // Spazio per Bottom Nav
            }
        }
    }
}

@Composable
private fun EmptyCategoryState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Nessun abbonamento in questa categoria",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp
        )
    }
}