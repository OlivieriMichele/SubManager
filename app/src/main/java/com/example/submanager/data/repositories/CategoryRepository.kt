package com.example.submanager.data.repositories

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Tv
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.submanager.data.database.CategoryDAOs
import com.example.submanager.data.database.CategoryEntity
import com.example.submanager.data.database.SubscriptionDAOs
import com.example.submanager.data.database.toCategory
import com.example.submanager.data.models.Category
import com.example.submanager.data.models.IconMapper
import com.example.submanager.data.models.Subscription
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

/**
 * Repository per la gestione delle Categories
 */
class CategoryRepository(
    private val categoryDao: CategoryDAOs,
    private val subscriptionDao: SubscriptionDAOs
) {
    val categories: Flow<List<Category>> = categoryDao
        .observeAll()
        .map { entities ->
            entities.map { entity ->
                val icon = IconMapper.nameToVector(entity.iconName)
                entity.toCategory(icon = icon)
            }
        }

    /**
     *   Calcola statistiche categorie basandosi sulle subscriptions
     */
    suspend fun getCategoriesWithStats(subscriptions: List<Subscription>): List<Category> {
        val allCategories = categoryDao.getAll()
        val group = subscriptions.groupBy { it.category }

        return allCategories.map { entity ->
            val icon = IconMapper.nameToVector(entity.iconName)
            val subs = group[entity.name] ?: emptyList()

            entity.toCategory(
                icon = icon,
                count = subs.size,
                total = subs.sumOf { it.price }
            )
        }
    }

    suspend fun addCategory(
        name: String,
        budget: Double,
        description: String,
        icon: ImageVector,
        gradientIndex: Int
    ) {
        val iconName = IconMapper.vectorToName(icon)

        val entity = CategoryEntity(
            name = name,
            iconName = iconName,
            budget = budget,
            description = description,
            gradientIndex
        )

        categoryDao.insert(entity)
    }

    suspend fun updateCategory(
        oldName: String,
        name: String,
        budget: Double,
        description: String,
        icon: ImageVector,
        gradientIndex: Int
    ) {
        val iconName = IconMapper.vectorToName(icon)

        if (oldName != name) { /* Todo */ }

        val entity = CategoryEntity(
            name = name,
            iconName = iconName,
            budget = budget,
            description = description,
            gradientIndex = gradientIndex
        )

        categoryDao.update(entity)
    }

    suspend fun deleteCategory(categoryName: String) {
        return categoryDao.deleteByName(categoryName)
    }

    fun getCategoryNames(): Flow<List<String>> {
        return categories.map { list -> list.map { it.name } }
    }

    suspend fun categoryExists(name: String): Boolean {
        return categoryDao.exist(name)
    }
}