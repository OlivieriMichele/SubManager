package com.example.submanager.data.repositories

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Tv
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.submanager.model.Category
import com.example.submanager.model.Subscription
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Repository per la gestione delle Categories
 */
class CategoryRepository() {
    private val _categories = MutableStateFlow(
        listOf(
            Category("Intrattenimento", 0, 0.0, Icons.Default.Tv, 20.0, "", 0),
            Category("Software", 0, 0.0, Icons.Default.Code, 50.0, "", 1),
            Category("Fitness", 0, 0.0, Icons.Default.FitnessCenter, 40.0, "", 2),
            Category("Shopping", 0, 0.0, Icons.Default.ShoppingCart, 50.0, "", 0),
            Category("Casa", 0, 0.0, Icons.Default.AccountBalance, 400.0, "Generiche spese per la casa", 0)
        )
    )

    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    // Calcola statistiche categorie basandosi sulle subscriptions
    fun getCategoriesWithStats(subscriptions: List<Subscription>): List<Category> {
        val groups = subscriptions.groupBy { it.category }
        return _categories.value.map { category ->
            val subs = groups[category.name] ?: emptyList()
            category.copy(
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
        if (_categories.value.any { it.name.equals(name, ignoreCase = true) }) {
            throw IllegalArgumentException("Categoria già esistente")
        }

        val newCategory = Category(name, 0, 0.0, icon, budget, description, gradientIndex)
        _categories.value = _categories.value + newCategory
    }

    suspend fun updateCategory(
        oldName: String,
        name: String,
        budget: Double,
        description: String,
        icon: ImageVector,
        gradientIndex: Int
    ) {
        _categories.value = _categories.value.map { category ->
            if (category.name == oldName) {
                category.copy(
                    name = name,
                    budget = budget,
                    description = description,
                    icon = icon,
                    gradientIndex = gradientIndex
                )
            } else {
                category
            }
        }
    }

    suspend fun deleteCategory(categoryName: String) {
        _categories.value = _categories.value.filterNot { it.name == categoryName }
    }

    fun getCategoryNames(): List<String> = _categories.value.map { it.name }

    fun categoryExists(name: String): Boolean =
        _categories.value.any { it.name.equals(name, ignoreCase = true) }
}