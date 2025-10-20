package com.example.submanager

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
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
                onToggleDarkMode = viewModel::toggleDarkMode
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
    }
}