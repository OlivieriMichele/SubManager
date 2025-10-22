package com.example.submanager

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
    data object AddSubscription : Screen("add_subscription")
    data object ViewSubscription : Screen("view_subscription/{subscriptionId}") {
        fun createRoute(subscriptionId: Int) = "view_subscription/$subscriptionId"
    }
}

@Composable
fun SubNavigation(navController: NavHostController, viewModel: SubViewModel) {

    Scaffold (
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            // Mostra FAB solo in Home e Categories
            val currentRoute = navController.currentBackStackEntry?.destination?.route
            if (currentRoute == Screen.Home.route || currentRoute == Screen.Categories.route) {
                FloatingActionButton(
                    onClick = { navController.navigate(Screen.AddSubscription.route) },
                    shape = CircleShape,
                    containerColor = Color.Transparent,
                    modifier = Modifier.background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF3B82F6),
                                Color(0xFF8B5CF6)
                            )
                        ),
                        shape = CircleShape
                    )
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Aggiungi abbonamento",
                        tint = Color.White
                    )
                }
            }
        }
    ){  paddingValue ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(paddingValue)
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
}