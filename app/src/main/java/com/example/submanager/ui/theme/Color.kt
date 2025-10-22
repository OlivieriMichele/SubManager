package com.example.submanager.ui.theme

import androidx.compose.ui.graphics.Color

// Colori di base (Dark Mode)
object DarkColors {
    val background = Color(0xFF09090B)      // zinc-950
    val surface = Color(0xCC18181B)         // zinc-900
    val surfaceAlpha = Color(0xFF1F1F21)    // zinc-900/80 (glassmorphism)
    val border = Color(0xFF27272A)          // zinc-800
    val textPrimary = Color.White
    val textSecondary = Color(0xFF71717A)   // gray-500
    val textTertiary = Color(0xFF52525B)    // gray-600
}

// Colori di base (Light Mode)
object LightColors {
    val background = Color(0xFFF9FAFB)      // gray-50
    val surface = Color.White
    val surfaceAlpha = Color(0xB3FFFFFF)    // white/70 (glassmorphism)
    val border = Color(0xFFE5E7EB)          // gray-200
    val textPrimary = Color(0xFF111827)     // gray-900
    val textSecondary = Color(0xFF6B7280)   // gray-600
    val textTertiary = Color(0xFF9CA3AF)    // gray-400
}

// Colori Accent e Pastel per il design del mockup (Gradients)
object AccentColors {
    // Colori principali del Main Card
    val mainGradientStart = Color(0xFF3B82F6).copy(alpha = 0.8f) // blue-500/80
    val mainGradientEnd = Color(0xFF8B5CF6).copy(alpha = 0.8f)   // purple-500/80
    val mainGradientLightStart = Color(0xFF93C5FD)              // blue-300
    val mainGradientLightEnd = Color(0xFFC4B5FD)                // purple-300

    // Colori Categorie (Corrispondenza con i gradienti Tailwind nel mockup)
    // light: from-blue-200 to-purple-200
    val lightBlue = Color(0xFFBFDBFE) // blue-200
    val lightPurple = Color(0xFFE9D5FF) // purple-200
    val lightIndigo = Color(0xFFC7D2FE) // indigo-200
    val lightPink = Color(0xFFFBCFE8) // pink-200

    // dark: from-blue-300 to-purple-300
    val darkBlue = Color(0xFF93C5FD) // blue-300
    val darkPurple = Color(0xFFD8B4FE) // purple-300
    val darkIndigo = Color(0xFFA5B4FC) // indigo-300
    val darkPink = Color(0xFFF9A8D4) // pink-300

    // Colori per le icone (Subscription Card)
    val pastelPurple = Color(0xFFAF88D5)// Netflix
    val pastelBlue = Color(0xFFA5C4FF)// Spotify
    val pastelIndigo = Color(0xFFC9BEE8)// Adobe CC
    val pastelPink = Color(0xFFC29BB0)// Amazon Prime
    val pastelYellow = Color(0xFFB4BE9F)// GitHub Pro
    val pastelGreen = Color(0xFF338057) // Planet Fitness
}