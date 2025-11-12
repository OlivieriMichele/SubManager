package com.example.submanager.data.database

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.submanager.data.models.Category
import com.example.submanager.data.models.Subscription
import java.time.LocalDate

@Entity(tableName = "subscriptions")
data class SubscriptionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val price: Double,
    val colorArgb: Int, // Color salvato come Int (ARGB)
    val nextBilling: Long, // LocalDate salvato come Long (Epoch Day)
    val category: String
)

fun SubscriptionEntity.toSubscription(): Subscription {
    return Subscription(
        id = id,
        name = name,
        price = price,
        color = Color(colorArgb),
        nextBilling = LocalDate.ofEpochDay(nextBilling),
        category = category
    )
}

fun Subscription.toEntity(): SubscriptionEntity {
    return SubscriptionEntity(
        id = id,
        name = name,
        price = price,
        colorArgb = color.toArgb(),
        nextBilling = nextBilling.toEpochDay(),
        category = category
    )
}

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val name: String,
    val iconName: String,  // Nome dell'icona
    val budget: Double,
    val description: String,
    val gradientIndex: Int
)

fun CategoryEntity.toCategory(
    icon: ImageVector,
    count: Int = 0,
    total: Double = 0.0
): Category {
    return Category(
        name = name,
        count = count,
        total = total,
        icon = icon,
        budget = budget,
        description = description,
        gradientIndex = gradientIndex
    )
}

fun Category.toEntity(iconName: String): CategoryEntity {
    return CategoryEntity(
        name = name,
        iconName = iconName,
        budget = budget,
        description = description,
        gradientIndex = gradientIndex
    )
}