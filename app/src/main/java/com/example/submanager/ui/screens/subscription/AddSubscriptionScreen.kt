package com.example.submanager.ui.screens.subscription

import androidx.compose.runtime.Composable

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