package com.example.submanager.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.submanager.data.models.Theme
import com.example.submanager.ui.screens.profile.components.ClearBiometricDialog
import com.example.submanager.ui.screens.profile.components.LogoutSection
import com.example.submanager.ui.screens.profile.components.PreferencesSection
import com.example.submanager.ui.screens.profile.components.ProfileCard
import com.example.submanager.ui.screens.profile.components.SecuritySection
import com.example.submanager.ui.screens.viewModel.AuthActions
import com.example.submanager.ui.screens.viewModel.AuthState
import com.example.submanager.ui.screens.viewModel.ThemeAction
import com.example.submanager.ui.screens.viewModel.ThemeState
import com.example.submanager.utils.NotificationHelper

@Composable
fun ProfileScreen(
    authState: AuthState,
    authActions: AuthActions,
    themeState: ThemeState,
    themeAction: ThemeAction,
    onNotificationsToggle: () -> Unit,
    onLogout: () -> Unit
) {
    var showClearBiometricDialog by remember { mutableStateOf(false) }
    var notificationsEnabled by remember { mutableStateOf(true) }
    val context = LocalContext.current // Per il test

    val isAutoTheme = themeState.isAutoTheme
    val currentManualTheme = themeState.manualTheme

    if (showClearBiometricDialog) {
        ClearBiometricDialog(
            onDismiss = { showClearBiometricDialog = false },
            onConfirm = {
                authActions.disableBiometric()
                showClearBiometricDialog = false
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        ProfileCard(authState = authState)

        Spacer(modifier = Modifier.height(24.dp))

        PreferencesSection(
            isAutoTheme = isAutoTheme,
            currentManualTheme = currentManualTheme,
            notificationsEnabled = notificationsEnabled,
            onThemeToggle = { themeAction.toggleAutoTheme() },
            onThemeChange = { newTheme ->
                themeAction.changeManualTheme(newTheme)
            },
            onNotificationsToggle = {
                notificationsEnabled = !notificationsEnabled
                onNotificationsToggle()
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        SecuritySection(
            authState = authState,
            onClearBiometricData = { showClearBiometricDialog = true }
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                NotificationHelper.showSubscriptionReminderNotification(
                    context = context,
                    subscriptionId = 5,
                    subscriptionName = "Test Netflix",
                    price = 13.99,
                    daysUntilRenewal = 1
                )
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiary
            )
        ) {
            Text("Test Notifica")
        }

        Spacer(modifier = Modifier.height(24.dp))

        LogoutSection(
            onLogout = onLogout
        )

        Spacer(modifier = Modifier.height(40.dp))
    }
}