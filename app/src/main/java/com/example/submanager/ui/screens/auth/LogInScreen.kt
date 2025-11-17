package com.example.submanager.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.example.submanager.ui.screens.auth.components.*
import com.example.submanager.ui.screens.viewModel.AuthActions
import com.example.submanager.ui.screens.viewModel.AuthState
import com.example.submanager.utils.BiometricAuthManager

@Composable
fun LoginScreen(
    state: AuthState,
    actions: AuthActions,
    onRegisterClick: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? FragmentActivity

    val biometricManager = remember(activity) {
        activity?.let { BiometricAuthManager(it) }
    }

    LaunchedEffect(Unit) {
        biometricManager?.let { manager ->
            val isAvailable = manager.isBiometricAvailable()
            actions.checkBiometricAvailability(isAvailable)
        }
    }

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

            if (biometricManager?.isBiometricAvailable() == true) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = state.rememberMe,
                        onCheckedChange = { actions.toggleRememberMe() },
                        enabled = !state.isLoading
                    )
                    Text(
                        text = "Ricordami per accesso biometrico",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            ErrorText(state.error)

            AuthButton(
                text = "Accedi",
                onClick = actions::login,
                isLoading = state.isLoading
            )

            if (state.canUseBiometric) {
                Fingerprint(
                    onClick = {
                        biometricManager?.authenticate(
                            title = "Accesso con impronta",
                            subtitle = "Usa la tua impronta per accedere"
                        ) { result ->
                            actions.onBiometricResult(result)
                        }
                    },
                    enabled = !state.isLoading
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            TextButton(onClick = onRegisterClick) {
                Text("Non hai un account? Registrati")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}