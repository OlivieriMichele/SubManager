package com.example.submanager.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.submanager.model.Category
import com.example.submanager.model.Subscription

@Composable
fun CategoryDetailScreen(
    categoryName: String,
    isDark: Boolean,
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
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp))
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
private fun CategoryDetailCard(categoryData: Category) {
    // Uso diretto di MaterialTheme, la variabile isDark non è più necessaria qui
    val gradientColors = if (isSystemInDarkTheme()) {
        listOf(categoryData.darkGradientStart, categoryData.darkGradientEnd)
    } else {
        listOf(categoryData.lightGradientStart, categoryData.lightGradientEnd)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            // AGGIORNATO: surface / outline
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon with Gradient Background
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(Brush.linearGradient(gradientColors), RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = categoryData.icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Totale Mensile",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "€${String.format("%.2f", categoryData.total)}",
                fontSize = 32.sp,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${categoryData.count} abbonamenti attivi",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun CategorySubscriptionItem(subscription: Subscription) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon (Iniziali)
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(subscription.color, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = subscription.name.first().toString(),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Info
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = subscription.name,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null,
                    modifier = Modifier.size(12.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Rinnovo: ${subscription.nextBilling}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Price
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "€${String.format("%.2f", subscription.price)}",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "/mese",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // More Icon
        IconButton(onClick = { /* ... */ }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "Altro",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
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

@Composable
private fun InfoCard(categoryData: Category, subCount: Int) {
    val averagePrice = if (subCount > 0) categoryData.total / subCount else 0.0

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp)
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
            .padding(20.dp)
    ) {
        Text(
            text = "Info Categoria",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Spesa annuale
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "Spesa annuale", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = "€${String.format("%.2f", categoryData.total * 12)}", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
        }
        // AGGIORNATO: outline
        Divider(color = MaterialTheme.colorScheme.outline, thickness = 1.dp, modifier = Modifier.padding(vertical = 12.dp))

        // Media per servizio
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "Media per servizio", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = "€${String.format("%.2f", averagePrice)}", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}