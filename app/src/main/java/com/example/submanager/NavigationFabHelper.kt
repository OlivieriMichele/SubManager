package com.example.submanager

import com.example.submanager.ui.screens.FabType

object NavigationFabHelper {
    fun getFabType(route: String?, isEditing: Boolean): FabType {
        return when {
            route == Screen.Home.route -> FabType.ADD
            route == Screen.AddSubscription.route -> FabType.SAVE
            route?.startsWith("view_subscription/") == true -> {
                if (isEditing) FabType.SAVE else FabType.EDIT
            }
            else -> FabType.NONE
        }
    }

    fun getFabAction(
        route: String?,
        isEditing: Boolean,
        onAdd: () -> Unit,
        onEdit: () -> Unit,
        onSave: () -> Unit
    ): () -> Unit {
        return when (route) {
            Screen.Home.route -> onAdd
            Screen.AddSubscription.route -> onSave
            Screen.ViewSubscription.route -> {
                if (isEditing) onSave else onEdit
            }
            else -> {{}}
        }
    }
}