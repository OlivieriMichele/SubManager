package com.example.submanager.ui.screens.insights.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SummaryCard(
    mostExpensiveSubscription: String,
    mostExpensiveCategory: String,
    yearlySpending: Double,
    potentialSavings: Double
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
            .padding(20.dp)
    ) {
        Text(
            text = "Riepilogo",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Abbonamento più costoso
        SummaryRow(
            label = "Abbonamento più costoso",
            value = mostExpensiveSubscription,
            subtitle = "€29.99/mese"
        )

        Divider(
            color = MaterialTheme.colorScheme.outline,
            thickness = 1.dp,
            modifier = Modifier.padding(vertical = 12.dp)
        )

        // Categoria più costosa
        SummaryRow(
            label = "Categoria più costosa",
            value = mostExpensiveCategory,
            subtitle = "€29.99/mese"
        )

        Divider(
            color = MaterialTheme.colorScheme.outline,
            thickness = 1.dp,
            modifier = Modifier.padding(vertical = 12.dp)
        )

        // Spesa annuale prevista
        SummaryRow(
            label = "Spesa annuale prevista",
            value = "€${String.format("%.2f", yearlySpending)}"
        )

        Divider(
            color = MaterialTheme.colorScheme.outline,
            thickness = 1.dp,
            modifier = Modifier.padding(vertical = 12.dp)
        )

        // Risparmio potenziale
        SummaryRow(
            label = "Risparmio potenziale",
            value = "€${String.format("%.2f", potentialSavings)}/anno",
            valueColor = Color(0xFF4CAF50)
        )
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    subtitle: String? = null,
    valueColor: Color? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Column(horizontalAlignment = androidx.compose.ui.Alignment.End) {
            Text(
                text = value,
                fontSize = 14.sp,
                color = valueColor ?: MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}