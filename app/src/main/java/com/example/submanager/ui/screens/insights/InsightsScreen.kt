package com.example.submanager.ui.screens.insights

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.submanager.model.MonthData
import com.example.submanager.ui.screens.insights.components.BudgetComparisonCard
import com.example.submanager.ui.screens.insights.components.CategoryDistributionCard
import com.example.submanager.ui.screens.insights.components.ComparisonCard
import com.example.submanager.ui.screens.insights.components.MonthlyTrendCard
import com.example.submanager.ui.screens.insights.components.SummaryCard

@Composable
fun InsigthsScreen(
    totalMonthly: Double,
    lastMonthTotal: Double,
    categories: List<Category>,
    last5MonthsData: List<MonthData>, // Todo: just for test, take from database
    onNavigateBack: () -> Unit
) {
    val percentageChange = if (lastMonthTotal > 0) {
        ((totalMonthly - lastMonthTotal) / lastMonthTotal * 100)
    } else {
        0.0
    }

    val averagePerService = categories.sumOf { it.total } / categories.sumOf { it.count }.coerceAtLeast(1)

    val mostExpensiveSubscription = "Planet Fitness" // Todo: Da calcolare dal ViewModel
    val mostExpensiveCategory = categories.maxByOrNull { it.total }?.name ?: "N/A"
    val yearlySpending = totalMonthly * 12
    val potentialSavings = 59.88 // Todo: Da calcolare

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
                .padding(top = 16.dp, bottom = 24.dp),
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
            Column {
                Text(
                    text = "Statistiche",
                    fontSize = 32.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Analisi della tua spesa",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Content
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // Comparison Cards Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ComparisonCard(
                        modifier = Modifier.weight(1f),
                        title = "vs mese scorso",
                        value = if (percentageChange >= 0) "+€${String.format("%.2f", percentageChange)}" else "-€${String.format("%.2f", -percentageChange)}",
                        percentage = "${if (percentageChange >= 0) "+" else ""}${String.format("%.0f", percentageChange)}%",
                        isPositive = percentageChange < 0
                    )

                    ComparisonCard(
                        modifier = Modifier.weight(1f),
                        title = "Media per servizio",
                        value = "€${String.format("%.2f", averagePerService)}",
                        subtitle = "8 servizi"
                    )
                }
            }

            item {
                // Category Distribution Chart
                CategoryDistributionCard(categories = categories)
            }

            item {
                // Last 5 Months Trend
                MonthlyTrendCard(monthsData = last5MonthsData)
            }

            item {
                // Budget vs Spending Comparison
                BudgetComparisonCard(categories = categories)
            }

            item {
                // Summary Card
                SummaryCard(
                    mostExpensiveSubscription = mostExpensiveSubscription,
                    mostExpensiveCategory = mostExpensiveCategory,
                    yearlySpending = yearlySpending,
                    potentialSavings = potentialSavings
                )
            }

            item {
                Spacer(modifier = Modifier.height(100.dp)) // Spazio per Bottom Nav
            }
        }
    }
}