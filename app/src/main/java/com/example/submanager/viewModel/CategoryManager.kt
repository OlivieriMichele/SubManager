package com.example.submanager.viewModel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.submanager.model.Category
import com.example.submanager.model.Subscription

class CategoryManager {

    private val _categories = mutableStateOf(
        listOf(
            Category(
                name = "Intrattenimento",
                count = 0,
                total = 0.0,
                icon = Icons.Default.Tv,
                budget = 20.0,
                description = "",
                gradientIndex = 0
            ),
            Category(
                name = "Software",
                count = 0,
                total = 0.0,
                icon = Icons.Default.Code,
                budget = 50.0,
                description = "",
                gradientIndex = 1
            ),
            Category(
                name = "Fitness",
                count = 0,
                total = 0.0,
                icon = Icons.Default.FitnessCenter,
                budget = 40.0,
                description = "",
                gradientIndex = 2
            ),
            Category(
                name = "Shopping",
                count = 0,
                total = 0.0,
                icon = Icons.Default.ShoppingCart,
                budget = 50.0,
                description = "",
                gradientIndex = 0
            ),
            Category(
                name = "Casa",
                count = 0,
                total = 0.0,
                icon = Icons.Default.AccountBalance,
                budget = 400.0,
                description = "Generiche spese per la casa",
                gradientIndex = 0
            )
        )
    )

    fun getCategoriesState(subscriptions: List<Subscription>): State<List<Category>> {
        val groups = subscriptions.groupBy { it.category }
        val categoryStats = _categories.value.map { category ->
            val subs = groups[category.name] ?: emptyList()
            val count = subs.size
            val total = subs.sumOf { it.price }
            category.copy(count = count, total = total)
        }
        return mutableStateOf(categoryStats)
    }

    fun getCategoryDetails(subscriptions: List<Subscription>, categoryName: String): Category? {
        return getCategoriesState(subscriptions).value.find { it.name == categoryName }
    }

    fun addCategory(
        name: String,
        budget: Double,
        description: String,
        icon: ImageVector,
        gradientIndex: Int
    ) {
        if (_categories.value.any { it.name.equals(name, ignoreCase = true) }) {
            return
        }

        val newCategory = Category(
            name = name,
            count = 0,
            total = 0.0,
            icon = icon,
            budget = budget,
            description = description,
            gradientIndex = gradientIndex
        )

        _categories.value = _categories.value + newCategory
    }

    fun updateCategory(
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

    fun deleteCategory(categoryName: String) {
        _categories.value = _categories.value.filter { it.name != categoryName }
    }

    fun getCategoryNames(): List<String> {
        return _categories.value.map { it.name }
    }

    fun categoryExists(name: String): Boolean {
        return _categories.value.any { it.name.equals(name, ignoreCase = true) }
    }
}

