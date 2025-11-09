package com.example.submanager.ui.screens.subscription

import androidx.compose.runtime.Composable
import com.example.submanager.ui.screens.viewModel.SubscriptionActions
import com.example.submanager.ui.screens.viewModel.SubscriptionFormState

@Composable
fun AddSubscriptionScreen(
    state: SubscriptionFormState,
    actions: SubscriptionActions
) {
    SubscriptionFormScreen(
        state = state,
        actions = actions
    )
}