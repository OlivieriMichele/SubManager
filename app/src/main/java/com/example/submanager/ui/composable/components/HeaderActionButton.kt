package com.example.submanager.ui.composable.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.submanager.data.models.Theme

@Composable
fun HeaderActionButtons(
    currentTheme: Theme,
    onThemeClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Theme Toggle
        IconButton(
            onClick = onThemeClick,
            modifier = Modifier
                .size(30.dp)
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(30.dp))
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(30.dp))
        ) {
            Icon(
                imageVector = when(currentTheme) {
                    Theme.Light -> Icons.Default.LightMode
                    Theme.Dark -> Icons.Default.DarkMode
                    Theme.System -> Icons.Default.Contrast
                },
                contentDescription = "Cambia tema",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(18.dp)
            )
        }

        // Notifications
        IconButton(
            onClick = onNotificationsClick,
            modifier = Modifier
                .size(30.dp)
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(30.dp))
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(30.dp))
        ) {
            Icon(
                imageVector = Icons.Default.Logout,
                contentDescription = "LogOut",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}