package com.example.submanager.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.submanager.ui.screens.auth.components.*
import com.example.submanager.ui.screens.viewModel.AuthActions
import com.example.submanager.ui.screens.viewModel.AuthState

@Composable
fun LoginScreen(
    state: AuthState,
    actions: AuthActions,
    onRegisterClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            AuthLogo()
            Spacer(modifier = Modifier.height(32.dp))

            AuthHeader(
                title = "Abbonamenti",
                subtitle = "Gestisci i tuoi abbonamenti"
            )

            Spacer(modifier = Modifier.height(48.dp))

            EmailField(
                value = state.email,
                onValueChange = actions::updateEmail,
                enabled = !state.isLoading
            )

            Spacer(modifier = Modifier.height(20.dp))

            PasswordField(
                value = state.password,
                onValueChange = actions::updatePassword,
                enabled = !state.isLoading
            )

            Spacer(modifier = Modifier.height(24.dp))

            ErrorText(state.error)

            AuthButton(
                text = "Accedi",
                onClick = actions::login,
                isLoading = state.isLoading
            )

            Spacer(modifier = Modifier.weight(1f))

            TextButton(onClick = onRegisterClick) {
                Text("Non hai un account? Registrati")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}