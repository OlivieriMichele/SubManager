package com.example.submanager.ui.screens.categories.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.submanager.model.Category
import com.example.submanager.ui.theme.AccentColors

@Composable
fun CategoryBudgetCard(categoryData: Category) {
    val gradientColors = if (isSystemInDarkTheme()) {
        listOf(AccentColors.mainGradientStart, AccentColors.mainGradientEnd)
    } else {
        listOf(AccentColors.mainGradientLightStart, AccentColors.mainGradientLightEnd)
    }

    val budgetPercentage = if (categoryData.budget > 0) {
        (categoryData.total / categoryData.budget).coerceIn(0.0, 1.0).toFloat()
    } else {
        0f
    }

    val disponibile = (categoryData.budget - categoryData.total).coerceAtLeast(0.0)
    val utilizzoPercentuale = (budgetPercentage * 100).toInt()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Budget Mensile",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "€${String.format("%.2f", categoryData.budget)}",
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Spesa Attuale",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "€${String.format("%.2f", categoryData.total)}",
                            fontSize = 18.sp,
                            color = if (categoryData.total > categoryData.budget)
                                Color(0xFFFF6B6B)
                            else
                                Color(0xFF4CAF50),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Progress Bar
        Column {
            LinearProgressIndicator(
                progress = { budgetPercentage },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = if (categoryData.total > categoryData.budget)
                    Color(0xFFFF6B6B)
                else
                    Color(0xFF4CAF50),
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                strokeCap = StrokeCap.Round
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "$utilizzoPercentuale% utilizzato",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "€${String.format("%.2f", disponibile)} disponibile",
                    fontSize = 12.sp,
                    color = Color(0xFF4CAF50)
                )
            }
        }
    }
}