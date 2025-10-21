package com.example.submanager.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.submanager.model.Subscription

@Composable
fun SubscriptionItem(subscription: Subscription) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
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
                // CORRETTO: onSurface
                color = MaterialTheme.colorScheme.onSurface
            )
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null,
                    modifier = Modifier.size(12.dp),
                    // CORRETTO: onSurfaceVariant
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = subscription.nextBilling,
                    fontSize = 12.sp,
                    // CORRETTO: onSurfaceVariant
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = " • ",
                    // CORRETTO: outline
                    color = MaterialTheme.colorScheme.outline,
                    fontSize = 12.sp,
                )
                Text(
                    text = subscription.category,
                    fontSize = 12.sp,
                    // CORRETTO: onSurfaceVariant
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
                // CORRETTO: onSurface
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "/mese",
                fontSize = 12.sp,
                // CORRETTO: onSurfaceVariant
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}