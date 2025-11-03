package com.example.submanager.ui.screens.categories.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.submanager.model.Category

@Composable
fun InfoCard(categoryData: Category, subCount: Int) {
    val averagePrice = if (subCount > 0) categoryData.total / subCount else 0.0

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp)
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
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