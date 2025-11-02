package com.example.submanager.ui.screens.insights.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TrendingUp
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
import com.example.submanager.model.MonthData

@Composable
fun MonthlyTrendCard(monthsData: List<MonthData>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
            .padding(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.TrendingUp,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Andamento Ultimi 5 Mesi",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // Bar Chart
        BarChart(
            data = monthsData,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            barColor = Color(0xFFB8B5FF)
        )
    }
}

@Composable
private fun BarChart(
    data: List<MonthData>,
    modifier: Modifier = Modifier,
    barColor: Color
) {
    val maxValue = data.maxOfOrNull { it.total } ?: 100.0

    Column(modifier = modifier) {
        // Y-axis labels
        Row(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.width(40.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text("€${maxValue.toInt()}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("€${(maxValue * 0.75).toInt()}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("€${(maxValue * 0.5).toInt()}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("€${(maxValue * 0.25).toInt()}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("€0", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            // Bars
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                data.forEach { monthData ->
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val barHeight = ((monthData.total / maxValue) * 120).dp

                        Box(
                            modifier = Modifier
                                .width(32.dp)
                                .height(barHeight)
                                .background(barColor, RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = monthData.month,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}