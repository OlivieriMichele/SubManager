package com.example.submanager.ui.screens.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.submanager.data.models.Theme
import com.example.submanager.ui.screens.viewModel.AuthState

@Composable
fun ProfileCard(authState: AuthState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = getInitials(authState.email),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = getUserName(authState.email),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = authState.email,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = { /* TODO */ },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("✏️ Modifica Profilo")
            }
        }
    }
}

@Composable
fun PreferencesSection(
    isAutoTheme: Boolean,
    currentManualTheme: Theme,
    notificationsEnabled: Boolean,
    onThemeToggle: () -> Unit,
    onThemeChange: (Theme) -> Unit,
    onNotificationsToggle: () -> Unit
) {
    SectionTitle("PREFERENZE")
    Spacer(modifier = Modifier.height(12.dp))

    SettingItem(
        icon = "🌓",
        title = "Tema Automatico",
        subtitle = if (isAutoTheme) "Attivo" else "Manuale",
        trailing = {
            Switch(
                checked = isAutoTheme,
                onCheckedChange = { onThemeToggle() }
            )
        }
    )

    SettingItem(
        icon = "🎨",
        title = "Tema Manuale",
        subtitle = if (currentManualTheme == Theme.Light) "Light" else "Dark",
        enabled = !isAutoTheme,
        onClick = {
            if (!isAutoTheme) {
                val newTheme = if (currentManualTheme == Theme.Light) Theme.Dark else Theme.Light
                onThemeChange(newTheme)
            }
        },
        trailing = {
            ChevronRightIcon(enabled = !isAutoTheme)
        }
    )

    SettingItem(
        icon = "🔔",
        title = "Notifiche",
        subtitle = "Ricevi promemoria rinnovi",
        trailing = {
            Switch(
                checked = notificationsEnabled,
                onCheckedChange = { onNotificationsToggle() }
            )
        }
    )
}

@Composable
fun SecuritySection(
    authState: AuthState,
    onBiometricToggle: () -> Unit,
    onClearBiometricData: () -> Unit
) {
    SectionTitle("SICUREZZA")
    Spacer(modifier = Modifier.height(12.dp))

    SettingItem(
        icon = "🔐",
        title = "Face ID / Touch ID",
        subtitle = if (authState.canUseBiometric) "Attivo" else "Disattivo",
        trailing = {
            Switch(
                checked = authState.canUseBiometric,
                onCheckedChange = { onBiometricToggle() }
            )
        }
    )

    if (authState.canUseBiometric) {
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onClearBiometricData,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.error
            )
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Elimina Credenziali Biometriche")
        }
    }
}
