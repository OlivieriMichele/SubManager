package com.example.submanager.ui.screens.subscription

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.submanager.data.repositories.CategoryRepository
import com.example.submanager.data.repositories.SubscriptionRepository
import com.example.submanager.model.Subscription
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

data class SubscriptionFormState(
    val subscriptionId: Int? = null,  // null = CREATE, non-null = EDIT
    val serviceName: String = "",
    val price: String = "",
    val renewalDate: LocalDate = LocalDate.now(),
    val selectedCategory: String = "",
    val selectedColor: Color = Color.Blue,
    val availableCategories: List<String> = emptyList(),
    val isEditing: Boolean = true,  // false = VIEW mode
    val isSaving: Boolean = false,
    val error: String? = null,
    val validationError: String? = null
)

interface SubscriptionActions {
    fun setServiceName(name: String)
    fun setPrice(price: String)
    fun setRenewalDate(date: LocalDate)
    fun setCategory(category: String)
    fun setColor(color: Color)
    fun setEditMode(editing: Boolean)

    // CRUD operations
    fun loadSubscription(id: Int)
    fun saveSubscription(onSuccess: () -> Unit)
    fun deleteSubscription(id: Int, onSuccess: () -> Unit)
    fun resetForm()
}

/**
 * SubscriptionViewModel - Gestisce Add/Edit/View/Delete Subscription
 */
class SubscriptionViewModel(
    private val subscriptionRepository: SubscriptionRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SubscriptionFormState())
    val state: StateFlow<SubscriptionFormState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            categoryRepository.categories.collect { categories ->
                _state.update { currentState ->
                    currentState.copy(
                        availableCategories = categories.map { it.name }
                    )
                }
            }
        }
    }

    val actions = object : SubscriptionActions {
        override fun setServiceName(name: String) {
            _state.update {
                it.copy(serviceName = name, validationError = null)
            }
        }

        override fun setPrice(price: String) {
            if (price.isEmpty() || price.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
                _state.update {
                    it.copy(price = price, validationError = null)
                }
            }
        }

        override fun setRenewalDate(date: LocalDate) {
            _state.update { it.copy(renewalDate = date) }
        }

        override fun setCategory(category: String) {
            _state.update {
                it.copy(selectedCategory = category, validationError = null)
            }
        }

        override fun setColor(color: Color) {
            _state.update { it.copy(selectedColor = color) }
        }

        override fun setEditMode(editing: Boolean) {
            _state.update { it.copy(isEditing = editing) }
        }

        override fun loadSubscription(id: Int) {
            val subscription = subscriptionRepository.getSubscriptionById(id)

            if (subscription != null) {
                _state.update {
                    SubscriptionFormState(
                        subscriptionId = subscription.id,
                        serviceName = subscription.name,
                        price = subscription.price.toString(),
                        renewalDate = subscription.nextBilling,
                        selectedCategory = subscription.category,
                        selectedColor = subscription.color,
                        availableCategories = it.availableCategories,
                        isEditing = false  // VIEW mode di default
                    )
                }
            } else {
                _state.update {
                    it.copy(error = "Subscription non trovata")
                }
            }
        }

        override fun saveSubscription(onSuccess: () -> Unit) {
            val currentState = _state.value

            if (currentState.serviceName.isBlank()) {
                _state.update {
                    it.copy(validationError = "Il nome è obbligatorio")
                }
                return
            }

            val priceValue = currentState.price.toDoubleOrNull()
            if (priceValue == null || priceValue <= 0) {
                _state.update {
                    it.copy(validationError = "Inserisci un prezzo valido")
                }
                return
            }

            if (currentState.selectedCategory.isBlank()) {
                _state.update {
                    it.copy(validationError = "Seleziona una categoria")
                }
                return
            }

            _state.update { it.copy(isSaving = true, validationError = null) }

            viewModelScope.launch {
                try {
                    val subscription = Subscription(
                        id = currentState.subscriptionId ?: generateNewId(),
                        name = currentState.serviceName,
                        price = priceValue,
                        color = currentState.selectedColor,
                        nextBilling = currentState.renewalDate,
                        category = currentState.selectedCategory
                    )

                    if (currentState.subscriptionId != null) {
                        subscriptionRepository.updateSubscription(subscription)
                    } else {
                        subscriptionRepository.addSubscription(subscription)
                    }

                    _state.update { it.copy(isSaving = false, isEditing = false) }
                    onSuccess()

                } catch (e: Exception) {
                    _state.update {
                        it.copy(
                            isSaving = false,
                            validationError = "Errore nel salvataggio: ${e.message}"
                        )
                    }
                }
            }
        }

        override fun deleteSubscription(id: Int, onSuccess: () -> Unit) {
            viewModelScope.launch {
                try {
                    subscriptionRepository.deleteSubscription(id)
                    onSuccess()
                } catch (e: Exception) {
                    _state.update {
                        it.copy(error = "Errore nell'eliminazione: ${e.message}")
                    }
                }
            }
        }

        override fun resetForm() {
            _state.update { currentState ->
                SubscriptionFormState(
                    availableCategories = currentState.availableCategories
                )
            }
        }
    }

    private fun generateNewId(): Int {
        return (subscriptionRepository.subscriptions.value.maxOfOrNull { it.id } ?: 0) + 1
    }
}