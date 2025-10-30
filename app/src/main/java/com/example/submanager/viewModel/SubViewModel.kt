package com.example.submanager.viewModel

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import com.example.submanager.model.Subscription
import com.example.submanager.model.Category

class SubViewModel : ViewModel() {

    // Delegati per la gestione separata
    private val themeManager = ThemeManager()
    private val categoryManager = CategoryManager()
    private val subscriptionManager = SubscriptionManager()

    // THEME MANAGEMENT

    val isDark: State<Boolean> = themeManager.isDark
    fun toggleDarkMode() = themeManager.toggleDarkMode()

    // EDITING STATE

    val isEditingState: State<Boolean> = themeManager.isEditingState
    val saveTrigger: State<Int> = themeManager.saveTrigger
    val saveCategoryTrigger: State<Int> = themeManager.saveCategoryTrigger

    fun setEditingMode(editing: Boolean) = themeManager.setEditingMode(editing)
    fun resetEditingMode() = themeManager.resetEditingMode()
    fun triggerSave() = themeManager.triggerSave()
    fun resetSaveTrigger() = themeManager.resetSaveTrigger()
    fun triggerSaveCategory() = themeManager.triggerSaveCategory()
    fun resetSaveCategoryTrigger() = themeManager.resetSaveCategoryTrigger()

    // CATEGORY MANAGEMENT

    val categoriesState: State<List<Category>>
        get() = categoryManager.getCategoriesState(subscriptionManager.subscriptions.value)

    fun getCategoryDetails(categoryName: String) = categoryManager.getCategoryDetails(subscriptionManager.subscriptions.value, categoryName)
    fun getCategorySubscriptions(categoryName: String) = subscriptionManager.getCategorySubscriptions(categoryName)
    fun addCategory(name: String, budget: Double, description: String, icon: androidx.compose.ui.graphics.vector.ImageVector, gradientIndex: Int) =
        categoryManager.addCategory(name, budget, description, icon, gradientIndex)
    fun updateCategory(oldName: String, name: String, budget: Double, description: String, icon: androidx.compose.ui.graphics.vector.ImageVector, gradientIndex: Int) =
        categoryManager.updateCategory(oldName, name, budget, description, icon, gradientIndex).also {
            subscriptionManager.updateSubscriptionCategory(oldName, name)
        }
    fun deleteCategory(categoryName: String) {
        categoryManager.deleteCategory(categoryName)
        subscriptionManager.deleteSubscriptionsByCategory(categoryName)
    }
    fun getCategoryNames() = categoryManager.getCategoryNames()
    fun categoryExists(name: String) = categoryManager.categoryExists(name)

    // SUBSCRIPTION MANAGEMENT

    val subscriptions: State<List<Subscription>> = subscriptionManager.subscriptions

    fun getTotalMonthly() = subscriptionManager.getTotalMonthly()
    fun getTotalYearly() = subscriptionManager.getTotalYearly()
    fun getSubscriptionById(id: Int) = subscriptionManager.getSubscriptionById(id)
    fun addSubscription(subscription: Subscription) = subscriptionManager.addSubscription(subscription)
    fun updateSubscription(subscription: Subscription) = subscriptionManager.updateSubscription(subscription)
    fun deleteSubscription(id: Int) = subscriptionManager.deleteSubscription(id)
}