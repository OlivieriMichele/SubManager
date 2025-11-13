package com.example.submanager.data.models

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

object IconMapper {
    /**
     * Mappa nome -> ImageVector
     */
    private val iconMap: Map<String, ImageVector> = mapOf(
        // Categorie default
        "Tv" to Icons.Default.Tv,
        "Code" to Icons.Default.Code,
        "FitnessCenter" to Icons.Default.FitnessCenter,
        "ShoppingCart" to Icons.Default.ShoppingCart,
        "AccountBalance" to Icons.Default.AccountBalance,

        // Icone disponibili per nuove categorie
        "Build" to Icons.Default.Build,
        "Favorite" to Icons.Default.Favorite,
        "Email" to Icons.Default.Email,
        "DateRange" to Icons.Default.DateRange,
        "Face" to Icons.Default.Face,
        "Home" to Icons.Default.Home,
        "Star" to Icons.Default.Star,
        "Phone" to Icons.Default.Phone,
        "Lock" to Icons.Default.Lock,

        // Fallback
        "Default" to Icons.Default.Category
    )

    /**
     * Mappa inversa ImageVector → nome
     */
    private val nameMap: Map<ImageVector, String> = iconMap.entries
        .associate { (name, icon) -> icon to name }

    /**
     * Converte ImageVector → String (per salvare su DB)
     */
    fun vectorToName(icon: ImageVector): String {
        return nameMap[icon] ?: "Default"
    }

    /**
     * Converte String → ImageVector (per leggere da DB)
     */
    fun nameToVector(name: String): ImageVector {
        return iconMap[name] ?: Icons.Default.Category
    }

    /**
     * Ottieni lista di icone disponibili con i loro nomi
     */
    fun getAvailableIcons(): List<Pair<String, ImageVector>> {
        return listOf(
            "ShoppingCart" to Icons.Default.ShoppingCart,
            "Build" to Icons.Default.Build,
            "Favorite" to Icons.Default.Favorite,
            "Email" to Icons.Default.Email,
            "DateRange" to Icons.Default.DateRange,
            "Face" to Icons.Default.Face,
            "Home" to Icons.Default.Home,
            "Star" to Icons.Default.Star,
            "Phone" to Icons.Default.Phone,
            "Lock" to Icons.Default.Lock
        )
    }
}