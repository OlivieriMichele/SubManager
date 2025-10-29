package com.example.submanager.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import java.time.LocalDate

data class Subscription(
    val id: Int,
    val name: String,
    val price: Double,
    val color: Color,
    val nextBilling: LocalDate,
    val category: String
)

data class Category(
    val name: String,
    val count: Int,
    val total: Double,
    val icon: ImageVector,
    val budget: Double = 0.0,
    val description: String = "",
    val gradientIndex: Int = 0
)