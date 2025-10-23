package com.example.submanager

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.submanager.ui.screens.subscription.AddSubscriptionScreen
import com.example.submanager.ui.screens.subscription.ViewSubscriptionScreen
import com.example.submanager.ui.screens.CategoriesScreen
import com.example.submanager.ui.screens.CategoryDetailScreen
import com.example.submanager.ui.screens.HomeScreen
import com.example.submanager.viewModel.SubViewModel

sealed class Screen(val route: String) {
    data object Home : Screen("Home")
    data object Categories : Screen("categories")
    data object CategoryDetail : Screen("category_detail/{categoryName}") {
        fun createRoute(categoryName: String) = "category_detail/$categoryName"
    }
    data object AddSubscription : Screen("add_subscription")
    data object ViewSubscription : Screen("view_subscription/{subscriptionId}") {
        fun createRoute(subscriptionId: Int) = "view_subscription/$subscriptionId"
    }
}

@Composable
fun SubNavigation(navController: NavHostController, viewModel: SubViewModel) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                subscriptions = viewModel.subscriptions.value,
                totalMonthly = viewModel.getTotalMonthly(),
                categoriesCount = viewModel.categoriesState.value.filter { it.count > 0 }.size,
                isDark = viewModel.isDark.value,
                onNavigateToCategories = {
                    navController.navigate(Screen.Categories.route)
                },
                onToggleDarkMode = viewModel::toggleDarkMode,
                onSubscriptionClick = { subscriptionId ->
                    navController.navigate(Screen.ViewSubscription.createRoute(subscriptionId))
                }
            )
        }

        composable(Screen.Categories.route) {
            CategoriesScreen(
                categories = viewModel.categoriesState.value,
                isDark = viewModel.isDark.value,
                onNavigateBack = { navController.popBackStack() },
                onCategoryClick = { categoryName ->
                    navController.navigate(Screen.CategoryDetail.createRoute(categoryName))
                }
            )
        }

        composable(
            route = Screen.CategoryDetail.route,
            arguments = listOf(navArgument("categoryName") { type = NavType.StringType })
        ) { backStackEntry ->
            val categoryName = backStackEntry.arguments?.getString("categoryName")
            if (categoryName != null) {
                CategoryDetailScreen(
                    categoryName = categoryName,
                    isDark = viewModel.isDark.value,
                    onNavigateBack = { navController.popBackStack() },
                    getCategoryDetails = viewModel::getCategoryDetails,
                    getCategorySubscriptions = viewModel::getCategorySubscriptions
                )
            }
        }

        composable(Screen.AddSubscription.route) {
            AddSubscriptionScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onSubscriptionAdded = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.ViewSubscription.route,
            arguments = listOf(navArgument("subscriptionId") { type = NavType.IntType })
        ) { backStackEntry ->
            val subscriptionId = backStackEntry.arguments?.getInt("subscriptionId") ?: 0
            ViewSubscriptionScreen(
                viewModel = viewModel,
                subscriptionId = subscriptionId,
                onNavigateBack = { navController.popBackStack() },
                onSubscriptionDeleted = {
                    navController.popBackStack()
                }
            )
        }
    }
}