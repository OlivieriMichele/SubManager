package com.example.submanager.ui.screens.subscription

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.submanager.ui.screens.viewModel.SubscriptionActions
import com.example.submanager.ui.screens.viewModel.SubscriptionFormState

@Composable
fun ViewSubscriptionScreen(
    state: SubscriptionFormState,
    actions: SubscriptionActions
) {
    if(state.error != null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = state.error,
                color = MaterialTheme.colorScheme.error
            )
        }
    }

    if (state.subscriptionId == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }

    SubscriptionFormScreen(
        state = state,
        actions = actions
    )
}