package com.example.submanager.ui.screens.subscription

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.example.submanager.ui.screens.viewModel.SubscriptionActions
import com.example.submanager.ui.screens.viewModel.SubscriptionFormState

@Composable
fun AddSubscriptionScreen(
    state: SubscriptionFormState,
    actions: SubscriptionActions
) {
    LaunchedEffect(Unit) {
        actions.resetForm()
        actions.setEditMode(true)
    }
    SubscriptionFormScreen(
        state = state,
        actions = actions
    )
}