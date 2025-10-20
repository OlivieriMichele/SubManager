package com.example.submanager.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class Subscription(
    val id: Int,
    val name: String,
    val price: Double,
    val color: Color,
    val nextBilling: String,
    val category: String
)

data class Category(
    val name: String,
    val count: Int, // To-Do: capire cos'è questo campo
    val total: Double, // To-Do: stessa cosa
    val icon: ImageVector,
    val lightGradientStart: Color, // Colore inizio gradiente Light
    val lightGradientEnd: Color,   // Colore fine gradiente Light
    val darkGradientStart: Color,  // Colore inizio gradiente Dark
    val darkGradientEnd: Color     // Colore fine gradiente Dark
)