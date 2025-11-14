package com.example.submanager.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.submanager.ui.screens.auth.components.*
import com.example.submanager.ui.screens.viewModel.AuthState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    state: AuthState,
    action: AuthAction
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
            /* Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    enabled = !state.isLoading
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Indietro",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
            } */

            AuthLogo()

            Spacer(modifier = Modifier.height(32.dp))

            AuthHeader(
                title = "Crea Account",
                subtitle = "Inizia a gestire i tuoi abbonamenti"
            )

            Spacer(modifier = Modifier.height(40.dp))

            EmailTextField(
                value = state.email,
                onValueChange = onEmailChange,
                enabled = !state.isLoading
            )

            Spacer(modifier = Modifier.height(20.dp))

            PasswordTextField(
                value = state.password,
                onValueChange = onPasswordChange,
                placeholder = "Minimo 6 caratteri",
                enabled = !state.isLoading
            )

            Spacer(modifier = Modifier.height(20.dp))

            PasswordTextField(
                value = state.confirmPassword,
                onValueChange = onConfirmPasswordChange,
                label = "Conferma Password",
                placeholder = "Ripeti la password",
                enabled = !state.isLoading
            )

            Spacer(modifier = Modifier.height(24.dp))

            ErrorMessage(message = state.error)

            AuthButton(
                text = "Registrati",
                onClick = onRegisterClick,
                isLoading = state.isLoading
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Registrandoti accetti i nostri Termini di Servizio e la Privacy Policy",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}