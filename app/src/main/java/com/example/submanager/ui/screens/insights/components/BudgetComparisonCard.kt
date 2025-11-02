package com.example.submanager.ui.screens.insights.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.submanager.model.Category

@Composable
fun BudgetComparisonCard(categories: List<Category>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
            .padding(20.dp)
    ) {
        Text(
            text = "Budget vs Spesa",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Grouped Bar Chart
        GroupedBarChart(
            categories = categories.take(4), // Mostra solo le prime 4 categorie
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Legend
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            LegendItem(color = Color(0xFF6BB6FF), label = "Budget")
            Spacer(modifier = Modifier.width(24.dp))
            LegendItem(color = Color(0xFFB8B5FF), label = "Spesa")
        }
    }
}

@Composable
private fun GroupedBarChart(
    categories: List<Category>,
    modifier: Modifier = Modifier
) {
    val maxValue = categories.maxOfOrNull { maxOf(it.budget, it.total) } ?: 100.0

    Column(modifier = modifier) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.width(40.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text("€${maxValue.toInt()}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("€${(maxValue * 0.66).toInt()}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("€${(maxValue * 0.33).toInt()}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("€0", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                categories.forEach { category ->
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            // Budget Bar
                            val budgetHeight = ((category.budget / maxValue) * 140).dp
                            Box(
                                modifier = Modifier
                                    .width(16.dp)
                                    .height(budgetHeight)
                                    .background(Color(0xFF6BB6FF), RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                            )

                            // Spending Bar
                            val spendingHeight = ((category.total / maxValue) * 140).dp
                            Box(
                                modifier = Modifier
                                    .width(16.dp)
                                    .height(spendingHeight)
                                    .background(Color(0xFFB8B5FF), RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = category.name.take(3),
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(12.dp, 8.dp)
                .background(color, RoundedCornerShape(2.dp))
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}