package com.example.submanager.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.submanager.data.models.Theme
import com.example.submanager.ui.screens.profile.components.ClearBiometricDialog
import com.example.submanager.ui.screens.profile.components.PreferencesSection
import com.example.submanager.ui.screens.profile.components.ProfileCard
import com.example.submanager.ui.screens.profile.components.SecuritySection
import com.example.submanager.ui.screens.viewModel.AuthActions
import com.example.submanager.ui.screens.viewModel.AuthState
import com.example.submanager.ui.screens.viewModel.ThemeState

@Composable
fun ProfileScreen(
    authState: AuthState,
    authActions: AuthActions,
    themeState: ThemeState,
    onThemeToggle: () -> Unit,
    onThemeChange: (Theme) -> Unit,
    onNotificationsToggle: () -> Unit,
) {
    var showClearBiometricDialog by remember { mutableStateOf(false) }
    var notificationsEnabled by remember { mutableStateOf(true) }

    val isAutoTheme = themeState.theme == Theme.System
    val currentManualTheme = if (!isAutoTheme) themeState.theme else Theme.Light

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
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        ProfileCard(authState = authState)

        Spacer(modifier = Modifier.height(24.dp))

        PreferencesSection(
            isAutoTheme = isAutoTheme,
            currentManualTheme = currentManualTheme,
            notificationsEnabled = notificationsEnabled,
            onThemeToggle = onThemeToggle,
            onThemeChange = onThemeChange,
            onNotificationsToggle = {
                notificationsEnabled = !notificationsEnabled
                onNotificationsToggle()
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        SecuritySection(
            authState = authState,
            onBiometricToggle = { authActions.toggleRememberMe(); authActions.disableBiometric() },
            onClearBiometricData = { showClearBiometricDialog = true }
        )

        Spacer(modifier = Modifier.height(80.dp))
    }
}