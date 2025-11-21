package com.example.submanager.ui.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StatsCards(
    subscriptionCount: Int,
    expiringCount: Int,
    categoriesCount: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp)
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
            .padding(vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatItem(
            value = subscriptionCount,
            label = "Attivi",
            color = Color(0xFF60A5FA)
        )
        // Separator
        Divider(
            modifier = Modifier
                .height(40.dp)
                .width(1.dp),
            color = MaterialTheme.colorScheme.outline
        )
        StatItem(
            value = expiringCount,
            label = "In scadenza",
            color = Color(0xFF818CF8)
        )
        // Separator
        Divider(
            modifier = Modifier
                .height(40.dp)
                .width(1.dp),
            color = MaterialTheme.colorScheme.outline
        )
        StatItem(
            value = categoriesCount,
            label = "Categorie",
            color = Color(0xFFC084FC)
        )
    }
}

@Composable
private fun StatItem(value: Int, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value.toString(),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}