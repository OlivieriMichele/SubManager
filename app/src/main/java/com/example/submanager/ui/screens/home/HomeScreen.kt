package com.example.submanager.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
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
    state: HomeState,
    actions: HomeActions,
    onNavigateToCategories: () -> Unit,
    onSubscriptionClick: (Int) -> Unit = {},
    onNavigateToInsights: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 32.dp)
            )
            return@Column
        }

        if (state.error != null) {
            Text(
                text = "Errore: ${state.error}",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(16.dp)
            )
            return@Column
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))

                // Main Card
                MainCard(
                    totalMonthly = state.totalMonthly,
                    totalYearly = state.totalYearly,
                    onNavigateToInsights = onNavigateToInsights
                )

                // Stats Cards
                StatsCards(
                    subscriptionCount = state.subscriptions.size,
                    expiringCount = state.expiringCount,
                    categoriesCount = state.activeCategoriesCount
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
            items(state.subscriptions) { sub ->
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