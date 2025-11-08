package com.example.submanager.ui.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Oggetto condiviso per icone e gradienti delle categorie
 * Temporanea finchè non trovo un modo migliore di gestirlo
 */
object CategoryIcons {
    val availableIcons: List<ImageVector> = listOf(
        Icons.Default.ShoppingCart,
        Icons.Default.Build,
        Icons.Default.Favorite,
        Icons.Default.Email,
        Icons.Default.DateRange,
        Icons.Default.Face,
        Icons.Default.Home,
        Icons.Default.Star,
        Icons.Default.Phone,
        Icons.Default.Lock
    )

    val gradientOptions: List<List<Color>> = listOf(
        listOf(Color(0xFFB8B5FF), Color(0xFFDED9FF)), // Viola
        listOf(Color(0xFFD9B5FF), Color(0xFFEFDEFF)), // Rosa
        listOf(Color(0xFFB5D9FF), Color(0xFFDEF0FF)), // Blu
        listOf(Color(0xFFB5FFD9), Color(0xFFDEFFF0)), // Verde
        listOf(Color(0xFFFFD9B5), Color(0xFFFFF0DE)), // Arancione
        listOf(Color(0xFFFFB5D9), Color(0xFFFFDEF0))  // Rosa chiaro
    )
}