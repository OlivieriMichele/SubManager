package com.example.submanager.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.submanager.model.Subscription

@Composable
fun HomeScreen(
    subscriptions: List<Subscription>,
    totalMonthly: Double,
    categoriesCount: Int,
    onNavigateToCategories: () -> Unit,
    onToggleDarkMode: () -> Unit,
    onSubscriptionClick: (Int) -> Unit = {},
    onNavigareToInsights: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))

                // Main Card
                MainCard(
                    totalMonthly = totalMonthly,
                    totalYearly = totalMonthly * 12,
                    onNavigateToInsights = onNavigareToInsights
                )

                // Stats Cards
                StatsCards(
                    subscriptionCount = subscriptions.size,
                    expiringCount = 2, // Valore fisso del mockup
                    categoriesCount = categoriesCount
                )

                // Categories Button
                CategoriesButton(onNavigateToCategories)

                // Subscriptions List Title
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Prossimi Rinnovi",
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.SemiBold
                    )
                    TextButton(onClick = { /* TODO: Vedi tutti */ }) {
                        Text(
                            text = "Vedi tutti",
                            color = Color(0xFF60A5FA), //Todo: use colorScheme
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // Subscriptions List
            items(subscriptions) { sub ->
                SubscriptionItem(
                    subscription = sub,
                    onClick = { onSubscriptionClick(sub.id) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                Spacer(modifier = Modifier.height(120.dp)) // Spazio per la Navigation Bar
            }
        }
    }
}