package com.example.submanager.ui.screens.subscription.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.submanager.ui.theme.AccentColors

@Composable
fun IconPreview(
    serviceName: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(100.dp)
            .background(color, RoundedCornerShape(24.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (serviceName.isNotEmpty()) serviceName.first().uppercase() else "+",
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
fun ColorPicker(
    selectedColor: Color,
    availableColors: List<Color>,
    onColorSelected: (Color) -> Unit,
    enabled: Boolean = true
) {
    Column {
        Text(
            text = "Colore",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(availableColors) { color ->
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(color, RoundedCornerShape(12.dp))
                        .border(
                            width = 3.dp,
                            color = if (selectedColor == color) {
                                MaterialTheme.colorScheme.onBackground
                            } else Color.Transparent,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable(
                            enabled = enabled,
                            onClick = { onColorSelected(color) },
                            indication = null, // QUESTO RISOLVE IL PROBLEMA
                            interactionSource = remember { MutableInteractionSource() }
                        )
                )
            }
        }
    }
}

// Colori predefiniti
object SubscriptionColors {
    val availableColors = listOf(
        AccentColors.pastelPurple,
        AccentColors.magenta,
        AccentColors.pastelBlue,
        AccentColors.azure,
        AccentColors.bue,
        AccentColors.pastelIndigo,
        AccentColors.pastelPink,
        AccentColors.pastelYellow,
        AccentColors.pastelGreen,
        AccentColors.cerulean
    )
}