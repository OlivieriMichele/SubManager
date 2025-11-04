package com.example.submanager.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect // Importazione per SideEffect
import androidx.compose.ui.graphics.Color // Importazione per Color.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView // Importazione per LocalView
import androidx.core.view.WindowCompat // Importazione per WindowCompat

// --- SCHEMI COLORE AGGIORNATI ---

private val DarkColorScheme = darkColorScheme(
    // Colori principali (Accent)
    primary = AccentColors.mainGradientEnd, // Ad esempio, il viola del main gradient
    secondary = AccentColors.mainGradientStart, // Ad esempio, il blu del main gradient
    tertiary = AccentColors.pastelPurple, // Uno dei colori pastello

    // Colori di sfondo e superficie
    background = DarkColors.background,
    surface = DarkColors.surface,

    // Colori "On" (testo/icone sui colori principali)
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = DarkColors.textPrimary,

    // Colori "On" (testo/icone sugli sfondi)
    onBackground = DarkColors.textPrimary,
    onSurface = DarkColors.textPrimary,
)

private val LightColorScheme = lightColorScheme(
    // Colori principali (Accent)
    primary = AccentColors.mainGradientLightEnd,
    secondary = AccentColors.mainGradientLightStart,
    tertiary = AccentColors.pastelPurple,

    // Colori di sfondo e superficie
    background = LightColors.background,
    surface = LightColors.surface,

    // Colori "On" (testo/icone sui colori principali)
    // Poiché i colori Light sono pastello (chiari), il testo onPrimary deve essere scuro
    onPrimary = LightColors.textPrimary,
    onSecondary = LightColors.textPrimary,
    onTertiary = LightColors.textPrimary,

    // Colori "On" (testo/icone sugli sfondi)
    onBackground = LightColors.textPrimary,
    onSurface = LightColors.textPrimary,
)

// --- FINE SCHEMI COLORE ---


@Composable
fun SubManagerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // Blocco per SideEffect e WindowCompat
    val view = LocalView.current
    if (!view.isInEditMode) {
        // Questa chiamata risolve Unresolved reference: SideEffect, WindowCompat e Variable expected
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Assicurati che Typography sia definito in Type.kt
        content = content
    )
}