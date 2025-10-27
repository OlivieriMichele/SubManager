package com.example.submanager.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.submanager.model.Category
import com.example.submanager.model.Subscription
import com.example.submanager.ui.screens.categoryDetail.CategoryDetailCard
import com.example.submanager.ui.screens.categoryDetail.CategorySubscriptionItem
import com.example.submanager.ui.screens.categoryDetail.InfoCard

@Composable
fun CategoryDetailScreen(
    categoryName: String,
    onNavigateBack: () -> Unit,
    getCategoryDetails: (String) -> Category?, // Funzione di logica passata come callback
    getCategorySubscriptions: (String) -> List<Subscription> // Funzione di logica passata come callback
) {
    val categoryData = getCategoryDetails(categoryName)
    val categorySubs = getCategorySubscriptions(categoryName)

    if (categoryData == null) {
        // Gestione errore o caricamento
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Status Bar Placeholder
        Spacer(
            modifier = Modifier
                .height(30.dp)
                .background(MaterialTheme.colorScheme.background)
        )

        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 16.dp, bottom = 32.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(20.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(20.dp))
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Indietro",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = categoryName,
                fontSize = 32.sp,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold
            )
        }

        // Content
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            item {
                // Category Detail Card
                CategoryDetailCard(categoryData)
                Spacer(modifier = Modifier.height(24.dp))

                // Abbonamenti Title
                Text(
                    text = "Abbonamenti",
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // Subscriptions List
            if (categorySubs.isNotEmpty()) {
                items(categorySubs) { sub ->
                    // To-Do instead of CategorySubscriptionItem use SubscriptionItem but optimize it
                    CategorySubscriptionItem(subscription = sub)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            } else {
                item {
                    EmptyCategoryState()
                }
            }

            item {
                // Info Card
                InfoCard(categoryData, categorySubs.size)
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