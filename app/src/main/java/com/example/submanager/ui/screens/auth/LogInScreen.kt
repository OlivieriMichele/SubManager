package com.example.submanager.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.submanager.ui.screens.auth.components.*

@Composable
fun LoginScreen(
    state: AuthState,
    action: AuthAction,
    onBiometricClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            AuthLogo()

            Spacer(modifier = Modifier.height(32.dp))

            AuthHeader(
                title = "Abbonamenti",
                subtitle = "Gestisci i tuoi abbonamenti in modo semplice"
            )

            Spacer(modifier = Modifier.height(48.dp))

            EmailTextField(
                value = state.email,
                onValueChange = onEmailChange,
                enabled = !state.isLoading
            )

            Spacer(modifier = Modifier.height(20.dp))

            PasswordTextField(
                value = state.password,
                onValueChange = onPasswordChange,
                enabled = !state.isLoading
            )

            TextButton(
                onClick = onForgotPasswordClick,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(
                    text = "Password dimenticata?",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            ErrorMessage(message = state.error)

            AuthButton(
                text = "Accedi",
                onClick = onLoginClick,
                isLoading = state.isLoading
            )

            Spacer(modifier = Modifier.height(24.dp))

            AuthDivider()

            Spacer(modifier = Modifier.height(24.dp))

            if (state.biometricAvailable) {
                AuthOutlinedButton(
                    text = "Accedi con Impronta",
                    onClick = onBiometricClick,
                    icon = Icons.Default.Fingerprint,
                    enabled = !state.isLoading
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            AuthTextLink(
                normalText = "Non hai un account? ",
                linkText = "Registrati",
                onClick = onRegisterClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            AuthFooter()

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}