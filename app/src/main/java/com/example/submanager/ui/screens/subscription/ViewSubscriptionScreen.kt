package com.example.submanager.ui.screens.subscription

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.submanager.viewModel.SubViewModel

@Composable
fun ViewSubscriptionScreen(
    viewModel: SubViewModel,
    subscriptionId: Int,
    onNavigateBack: () -> Unit
) {
    // Trova l'abbonamento dal ViewModel
    val subscription = viewModel.subscriptions.value.find { it.id == subscriptionId }

    if (subscription != null) {
        SubscriptionFormScreen(
            viewModel = viewModel,
            mode = FormMode.VIEW,
            subscription = subscription,
            onSave = { updatedSub ->
                viewModel.updateSubscription(updatedSub)
                onNavigateBack()
            }
        )
    } else {
        // Fallback se l'abbonamento non esiste
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Abbonamento non trovato",
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}