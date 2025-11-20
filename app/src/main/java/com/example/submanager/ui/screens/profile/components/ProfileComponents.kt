package com.example.submanager.ui.screens.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Mode
import androidx.compose.material.icons.filled.Notifications
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
import com.example.submanager.ui.theme.AccentColors

@Composable
fun ProfileCard(authState: AuthState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        border = CardDefaults.outlinedCardBorder(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column (
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ){
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary), //Todo linear gradient
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = getInitials(authState.email),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
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

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = { /* TODO */ },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = AccentColors.mainGradientStart
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Mode,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Text("Modifica profilo", fontSize = 14.sp)
                }
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
        icon = Icons.Default.Contrast,
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
        icon = Icons.Default.DarkMode,
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
        icon = Icons.Default.Notifications,
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
        icon = Icons.Default.Fingerprint,
        title = "Face ID / Touch ID",
        subtitle = if (authState.canUseBiometric) "Attivo" else "Disattivo",
        trailing = {
            Switch(
                checked = authState.canUseBiometric,
                onCheckedChange = { onClearBiometricData() }
            )
        }
    )
    /*
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
     */
}
