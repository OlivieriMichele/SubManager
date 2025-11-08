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
    state: InsightsState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
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
                        value = if (state.percentageChange >= 0)
                            "+€${String.format("%.2f", state.percentageChange)}"
                        else
                            "-€${String.format("%.2f", -state.percentageChange)}",
                        percentage = "${if (state.percentageChange >= 0) "+" else ""}${String.format("%.0f", state.percentageChange)}%",
                        isPositive = state.percentageChange < 0
                    )

                    ComparisonCard(
                        modifier = Modifier.weight(1f),
                        title = "Media per servizio",
                        value = "€${String.format("%.2f", state.averagePerService)}",
                        subtitle = "8 servizi"
                    )
                }
            }

            item {
                // Category Distribution Chart
                CategoryDistributionCard(categories = state.categories)
            }

            item {
                // Last 5 Months Trend
                MonthlyTrendCard(monthsData = state.last5MonthsData)
            }

            item {
                // Budget vs Spending Comparison
                BudgetComparisonCard(categories = state.categories)
            }

            item {
                // Summary Card
                SummaryCard(
                    mostExpensiveSubscription = state.mostExpensiveSubscription ?: "N/A",
                    mostExpensiveCategory = state.mostExpensiveCategory ?: "N/A",
                    yearlySpending = state.totalMonthly,
                    potentialSavings = state.potentialSavings
                )
            }

            item {
                Spacer(modifier = Modifier.height(100.dp)) // Spazio per Bottom Nav
            }
        }
    }
}