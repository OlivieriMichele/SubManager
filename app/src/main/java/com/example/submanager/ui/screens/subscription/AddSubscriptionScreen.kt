package com.example.submanager.ui.screens.subscription

import androidx.compose.runtime.Composable
import com.example.submanager.viewModel.SubViewModel

@Composable
fun AddSubscriptionScreen(
    viewModel: SubViewModel,
    onNavigateBack: () -> Unit,
    onSubscriptionAdded: () -> Unit
) {
    SubscriptionFormScreen(
        viewModel = viewModel,
        mode = FormMode.CREATE,
        onNavigateBack = onNavigateBack,
        onSave = { newSub ->
            viewModel.addSubscription(newSub)
            onSubscriptionAdded()
        }
    )
}