package com.example.submanager.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.submanager.ui.screens.home.components.CategoriesButton
import com.example.submanager.ui.screens.home.components.MainCard
import com.example.submanager.ui.screens.home.components.StatsCards
import com.example.submanager.ui.screens.home.components.SubscriptionListHeader
import com.example.submanager.ui.screens.viewModel.HomeActions
import com.example.submanager.ui.screens.viewModel.HomeState

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
                    subscriptionCount = state.allSubscriptions.size,
                    expiringCount = state.expiringCount,
                    categoriesCount = state.activeCategoriesCount
                )

                // Categories Button
                CategoriesButton(onNavigateToCategories)

                // Subscriptions List Title with Filter
                SubscriptionListHeader(
                    currentFilterName = state.currentFilterName,
                    actions = actions
                )
            }

            // Subscriptions List
            items(state.subscriptions) { sub ->
                SubscriptionItem(
                    subscription = sub,
                    onClick = { onSubscriptionClick(sub.id) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Empty state
            if (state.subscriptions.isEmpty()) {
                item {
                    Text(
                        text = "Nessun abbonamento",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}