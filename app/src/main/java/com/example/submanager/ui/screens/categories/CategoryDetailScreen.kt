package com.example.submanager.ui.screens.categoryDetail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.submanager.model.Category
import com.example.submanager.model.Subscription

@Composable
fun CategoryDetailScreen(
    categoryName: String,
    onNavigateBack: () -> Unit,
    getCategoryDetails: (String) -> Category?,
    getCategorySubscriptions: (String) -> List<Subscription>,
    onDeleteCategory: (String) -> Unit
) {
    val categoryData = getCategoryDetails(categoryName)
    val categorySubs = getCategorySubscriptions(categoryName)
    var showDeleteDialog by remember { mutableStateOf(false) }

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