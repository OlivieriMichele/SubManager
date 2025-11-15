package com.example.submanager.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.submanager.ui.screens.auth.components.*
import com.example.submanager.ui.screens.viewModel.AuthActions
import com.example.submanager.ui.screens.viewModel.AuthState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen(
    state: AuthState,
    actions: AuthActions,
    onBackClick: () -> Unit
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick, enabled = !state.isLoading) {
                    Icon(Icons.Default.ArrowBack, "Indietro")
                }
                Spacer(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(20.dp))

            AuthLogo()
            Spacer(modifier = Modifier.height(32.dp))

            AuthHeader(
                title = "Crea Account",
                subtitle = "Inizia a gestire i tuoi abbonamenti"
            )

            Spacer(modifier = Modifier.height(40.dp))

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

            Spacer(modifier = Modifier.height(20.dp))

            PasswordField(
                value = state.confirmPassword,
                onValueChange = actions::updateConfirmPassword,
                label = "Conferma Password",
                enabled = !state.isLoading
            )

            Spacer(modifier = Modifier.height(24.dp))

            ErrorText(state.error)

            AuthButton(
                text = "Registrati",
                onClick = actions::register,
                isLoading = state.isLoading
            )
        }
    }
}