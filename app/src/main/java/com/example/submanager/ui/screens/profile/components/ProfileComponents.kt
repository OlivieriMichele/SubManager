package com.example.submanager.ui.screens.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Mode
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Row con Avatar e Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar circolare
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(brush = Brush.linearGradient(listOf(AccentColors.mainGradientStart, AccentColors.mainGradientEnd))),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = getInitials(authState.email),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Colonna con info
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    // Nome utente
                    Text(
                        text = getUserName(authState.email),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    // Email
                    Text(
                        text = authState.email,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bottone Modifica Profilo (full width con border)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Mode,
                    contentDescription = null,
                    tint = AccentColors.mainGradientStart,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Modifica Profilo",
                    fontSize = 14.sp,
                    color = AccentColors.mainGradientStart,
                    fontWeight = FontWeight.Medium
                )
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
                onCheckedChange = { onThemeToggle() },
                colors = SwitchDefaults.colors(
                    checkedTrackColor = AccentColors.mainGradientEnd
                )
            )
        }
    )

    SettingItem(
        icon = if (currentManualTheme == Theme.Light) Icons.Default.LightMode else  Icons.Default.DarkMode,
        iconTint = AccentColors.mainGradientEnd,
        title = "Tema Manuale",
        subtitle = if (currentManualTheme == Theme.Light) "Light" else "Dark",
        enabled = !isAutoTheme,
        trailing = {
            Switch(
                checked = currentManualTheme == Theme.Dark,
                onCheckedChange = {
                    if (!isAutoTheme) {
                        val newTheme = if (currentManualTheme == Theme.Light) Theme.Dark else Theme.Light
                        onThemeChange(newTheme)
                    }
                },
                colors = SwitchDefaults.colors(
                    checkedTrackColor = AccentColors.darkIndigo
                )
            )
        }
    )

    SettingItem(
        icon = Icons.Default.Notifications,
        iconTint = AccentColors.mainGradientStart,
        title = "Notifiche",
        subtitle = "Ricevi promemoria rinnovi",
        trailing = {
            Switch(
                checked = notificationsEnabled,
                onCheckedChange = { onNotificationsToggle() },
                colors = SwitchDefaults.colors(
                    checkedTrackColor = AccentColors.mainGradientStart
                )
            )
        }
    )
}

@Composable
fun SecuritySection(
    authState: AuthState,
    onClearBiometricData: () -> Unit
) {
    SectionTitle("SICUREZZA")
    Spacer(modifier = Modifier.height(12.dp))

    SettingItem(
        icon = Icons.Default.Fingerprint,
        iconTint = Color(0xFF5DE361), // Verde
        title = "Face ID / Touch ID",
        subtitle = if (authState.canUseBiometric) "Attivo" else "Disattivo",
        trailing = {
            Switch(
                checked = authState.canUseBiometric,
                onCheckedChange = { onClearBiometricData() },
                colors = SwitchDefaults.colors(
                    checkedTrackColor = Color(0xFF4AB74E)
                )
            )
        }
    )
}

@Composable
fun LogoutSection(
    onLogout: () -> Unit
) {
    SectionTitle("ACCOUNT")
    Spacer(modifier = Modifier.height(12.dp))

    Button(
        onClick = onLogout,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = AccentColors.darkIndigo
        ),
        border = ButtonDefaults.outlinedButtonBorder
    ) {
        Icon(
            imageVector = Icons.Default.Logout,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Esci dall'account",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}