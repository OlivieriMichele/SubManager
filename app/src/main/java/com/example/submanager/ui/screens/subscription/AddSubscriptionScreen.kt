package com.example.submanager.ui.screens.subscription

import androidx.compose.runtime.Composable
import com.example.submanager.viewModel.SubViewModel

@Composable
fun AddSubscriptionScreen(
    viewModel: SubViewModel,
    onSubscriptionAdded: () -> Unit
) {
    SubscriptionFormScreen(
        viewModel = viewModel,
        mode = FormMode.CREATE,
        onSave = { newSub ->
            if (newSub.name != "") viewModel.addSubscription(newSub)
            onSubscriptionAdded()
        }
    )
}