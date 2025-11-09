package com.example.submanager.ui.screens.categories

import androidx.compose.material.icons.filled.Add
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.submanager.data.repositories.CategoryRepository
import com.example.submanager.data.repositories.SubscriptionRepository
import com.example.submanager.model.Category
import com.example.submanager.model.Subscription
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CategoryListState(
    val categories: List<Category> = emptyList(),
    val mostExpensiveCategory: String? = null,
    val categoryWithMostServices: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

data class CategoryDetailState(
    val category: Category? = null,
    val subscriptions: List<Subscription> = emptyList(),
    val averagePrice: Double = 0.0,
    val yearlySpending: Double = 0.0,
    val isLoading: Boolean = false,
    val error: String? = null
)

data class CategoryFormState(
    val name: String = "",
    val budget: String = "",
    val description: String = "",
    val selectedIconIndex: Int = 0,
    val selectedGradientIndex: Int = 0,
    val isSaving: Boolean = false,
    val error: String? = null,
    val validationError: String? = null
)

interface CategoryActions {
    // fun refreshCategories()
    fun loadCategoryDetail(categoryName: String)
    fun deleteCategory(categoryName: String, onSuccess: () -> Unit)

    // Form add/edit categoria
    fun setName(name: String)
    fun setBudget(budget: String)
    fun setDescription(description: String)
    fun setIconIndex(index: Int)
    fun setGradientIndex(index: Int)
    fun saveCategory(onSuccess: () -> Unit)
    fun resetForm()
}

/**
 * CategoryViewModel - Gestisce tutte le operazioni sulle categorie
 */
class CategoryViewModel(
    private val categoryRepository: CategoryRepository,
    private val subscriptionRepository: SubscriptionRepository
) : ViewModel() {

    val categoryListState: StateFlow<CategoryListState> = combine(
        subscriptionRepository.subscriptions,
        categoryRepository.categories
    ) { subscriptions, categories ->
        val categoriesWithStats = categoryRepository.getCategoriesWithStats(subscriptions)

        // Calcola statistiche
        val mostExpensive = categoriesWithStats.maxByOrNull { it.total }?.name
        val mostServices = categoriesWithStats.maxByOrNull { it.count }?.name

        CategoryListState(
            categories = categoriesWithStats,
            mostExpensiveCategory = mostExpensive,
            categoryWithMostServices = mostServices
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CategoryListState(isLoading = true)
    )

    private val _categoryDetailState = MutableStateFlow(CategoryDetailState())
    val categoryDetailState: StateFlow<CategoryDetailState> = _categoryDetailState.asStateFlow()

    private val _categoryFormState = MutableStateFlow(CategoryFormState())
    val categoryFormState: StateFlow<CategoryFormState> = _categoryFormState.asStateFlow()

    // ========== ACTIONS IMPLEMENTATION ==========

    val actions = object : CategoryActions {

        override fun loadCategoryDetail(categoryName: String) {
            viewModelScope.launch {
                val subscriptions = subscriptionRepository.subscriptions.value
                val categoriesWithStats = categoryRepository.getCategoriesWithStats(subscriptions)
                val category = categoriesWithStats.find { it.name == categoryName }
                val categorySubs = subscriptionRepository.getSubscriptionsByCategory(categoryName)

                if (category != null) {
                    val average = if (categorySubs.isNotEmpty()) {
                        category.total / categorySubs.size
                    } else {
                        0.0
                    }

                    _categoryDetailState.update {
                        CategoryDetailState(
                            category = category,
                            subscriptions = categorySubs,
                            averagePrice = average,
                            yearlySpending = category.total * 12
                        )
                    }
                } else {
                    _categoryDetailState.update {
                        it.copy(error = "Categoria non trovata")
                    }
                }
            }
        }

        override fun deleteCategory(categoryName: String, onSuccess: () -> Unit) {
            viewModelScope.launch {
                try {
                    // Elimina prima le subscriptions associate
                    subscriptionRepository.deleteSubscriptionsByCategory(categoryName)
                    categoryRepository.deleteCategory(categoryName)
                    onSuccess()
                } catch (e: Exception) {
                    _categoryDetailState.update {
                        it.copy(error = "Errore nell'eliminazione: ${e.message}")
                    }
                }
            }
        }

        // ========== FORM ACTIONS ==========

        override fun setName(name: String) {
            _categoryFormState.update {
                it.copy(name = name, validationError = null)
            }
        }

        override fun setBudget(budget: String) {
            // Valida che sia un numero valido
            if (budget.isEmpty() || budget.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
                _categoryFormState.update {
                    it.copy(budget = budget, validationError = null)
                }
            }
        }

        override fun setDescription(description: String) {
            _categoryFormState.update { it.copy(description = description) }
        }

        override fun setIconIndex(index: Int) {
            _categoryFormState.update { it.copy(selectedIconIndex = index) }
        }

        override fun setGradientIndex(index: Int) {
            _categoryFormState.update { it.copy(selectedGradientIndex = index) }
        }

        override fun saveCategory(onSuccess: () -> Unit) {
            val currentState = _categoryFormState.value

            if (currentState.name.isBlank()) {
                _categoryFormState.update {
                    it.copy(validationError = "Il nome è obbligatorio")
                }
                return
            }

            val budgetValue = currentState.budget.toDoubleOrNull()
            if (budgetValue == null || budgetValue < 0) {
                _categoryFormState.update {
                    it.copy(validationError = "Inserisci un budget valido")
                }
                return
            }

            // Verifica se categoria già esistente
            if (categoryRepository.categoryExists(currentState.name)) {
                _categoryFormState.update {
                    it.copy(validationError = "Categoria già esistente")
                }
                return
            }

            _categoryFormState.update { it.copy(isSaving = true, validationError = null) }

            viewModelScope.launch {
                try {
                    // Le icone devono essere passate dalla UI per ora usiamo un placeholder
                    categoryRepository.addCategory(
                        name = currentState.name,
                        budget = budgetValue,
                        description = currentState.description,
                        icon = androidx.compose.material.icons.Icons.Default.Add, // TODO: passa icon corretta
                        gradientIndex = currentState.selectedGradientIndex
                    )

                    _categoryFormState.update { it.copy(isSaving = false) }
                    resetForm()
                    onSuccess()

                } catch (e: Exception) {
                    _categoryFormState.update {
                        it.copy(
                            isSaving = false,
                            validationError = "Errore nel salvataggio: ${e.message}"
                        )
                    }
                }
            }
        }

        override fun resetForm() {
            _categoryFormState.value = CategoryFormState()
        }
    }

    /**
     * Helper per passare icon e gradient alla saveCategory
     * Chiamata dalla UI quando ha tutte le info necessarie
     */
    fun saveCategoryWithIcon(
        icon: ImageVector,
        onSuccess: () -> Unit
    ) {
        val currentState = _categoryFormState.value

        if (currentState.name.isBlank()) {
            _categoryFormState.update {
                it.copy(validationError = "Il nome è obbligatorio")
            }
            return
        }

        val budgetValue = currentState.budget.toDoubleOrNull()
        if (budgetValue == null || budgetValue < 0) {
            _categoryFormState.update {
                it.copy(validationError = "Inserisci un budget valido")
            }
            return
        }

        if (categoryRepository.categoryExists(currentState.name)) {
            _categoryFormState.update {
                it.copy(validationError = "Categoria già esistente")
            }
            return
        }

        _categoryFormState.update { it.copy(isSaving = true, validationError = null) }

        viewModelScope.launch {
            try {
                categoryRepository.addCategory(
                    name = currentState.name,
                    budget = budgetValue,
                    description = currentState.description,
                    icon = icon,
                    gradientIndex = currentState.selectedGradientIndex
                )

                _categoryFormState.update { it.copy(isSaving = false) }
                actions.resetForm()
                onSuccess()

            } catch (e: Exception) {
                _categoryFormState.update {
                    it.copy(
                        isSaving = false,
                        validationError = "Errore: ${e.message}"
                    )
                }
            }
        }
    }
}