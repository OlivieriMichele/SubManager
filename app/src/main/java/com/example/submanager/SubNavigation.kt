package com.example.submanager

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.toRoute
import com.example.submanager.model.MonthData
import com.example.submanager.ui.screens.categories.CategoryScreen
import com.example.submanager.ui.screens.categories.NewCategoryScreen
import com.example.submanager.ui.screens.categoryDetail.CategoryDetailScreen
import com.example.submanager.ui.screens.home.HomeScreen
import com.example.submanager.ui.screens.insights.InsigthsScreen
import com.example.submanager.ui.screens.subscription.AddSubscriptionScreen
import com.example.submanager.ui.screens.subscription.ViewSubscriptionScreen
import com.example.submanager.viewModel.SubViewModel
import kotlinx.serialization.Serializable

// Definizione type-safe delle route
sealed interface Screen {
    @Serializable
    data object Home : Screen

    @Serializable
    data object Categories : Screen

    @Serializable
    data class CategoryDetail(val categoryName: String) : Screen

    @Serializable
    data object AddSubscription : Screen

    @Serializable
    data class ViewSubscription(val subscriptionId: Int) : Screen

    @Serializable
    data object NewCategory : Screen

    @Serializable
    data object Insights : Screen
}

// Extension function per ottenere la schermata corrente in modo type-safe
fun NavBackStackEntry.getCurrentScreen(): Screen? {
    return destination.route?.let { route ->
        when {
            route.contains("Home") -> Screen.Home
            route.contains("Categories") && !route.contains("CategoryDetail") -> Screen.Categories
            route.contains("CategoryDetail") -> toRoute<Screen.CategoryDetail>()
            route.contains("AddSubscription") -> Screen.AddSubscription
            route.contains("ViewSubscription") -> toRoute<Screen.ViewSubscription>()
            route.contains("NewCategory") -> Screen.NewCategory
            route.contains("Insights") -> Screen.Insights
            else -> null
        }
    }
}

@Composable
fun SubNavigation(
    navController: NavHostController,
    viewModel: SubViewModel,
    modifier: Modifier
) {
    HandleNavigationEffects(navController, viewModel)

    NavHost(
        navController = navController,
        startDestination = Screen.Home,
        modifier = modifier
    ) {
        // Home Screen
        composable<Screen.Home> {
            HomeScreen(
                subscriptions = viewModel.subscriptions.value,
                totalMonthly = viewModel.getTotalMonthly(),
                categoriesCount = viewModel.categoriesState.value.filter { it.count > 0 }.size,
                onNavigateToCategories = {
                    navController.navigate(Screen.Categories)
                },
                onToggleDarkMode = viewModel::toggleDarkMode,
                onSubscriptionClick = { subscriptionId ->
                    navController.navigate(Screen.ViewSubscription(subscriptionId))
                },
                onNavigareToInsights = {
                    navController.navigate(Screen.Insights)
                }
            )
        }

        // Categories Screen
        composable<Screen.Categories> {
            CategoryScreen(
                categories = viewModel.categoriesState.value,
                onNavigateBack = { navController.popBackStack() },
                onCategoryClick = { categoryName ->
                    navController.navigate(Screen.CategoryDetail(categoryName))
                }
            )
        }

        // Category Detail Screen
        composable<Screen.CategoryDetail> { backStackEntry ->
            val route = backStackEntry.toRoute<Screen.CategoryDetail>()
            CategoryDetailScreen(
                categoryName = route.categoryName,
                onNavigateBack = { navController.popBackStack() },
                getCategoryDetails = viewModel::getCategoryDetails,
                getCategorySubscriptions = viewModel::getCategorySubscriptions,
                onDeleteCategory = { categoryName ->
                    viewModel.deleteCategory(categoryName)
                }
            )
        }

        // Add Subscription Screen
        composable<Screen.AddSubscription> {
            AddSubscriptionScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onSubscriptionAdded = {
                    navController.popBackStack()
                }
            )
        }

        // View Subscription Screen
        composable<Screen.ViewSubscription> { backStackEntry ->
            val route = backStackEntry.toRoute<Screen.ViewSubscription>()
            ViewSubscriptionScreen(
                viewModel = viewModel,
                subscriptionId = route.subscriptionId,
                onNavigateBack = { navController.popBackStack() },
                onSubscriptionDeleted = {
                    navController.popBackStack()
                }
            )
        }

        // New Category Screen
        composable<Screen.NewCategory> {
            NewCategoryScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onCategorySaved = { navController.popBackStack() }
            )
        }

        // Insights Screen
        composable<Screen.Insights> {
            InsigthsScreen(
                totalMonthly = viewModel.getTotalMonthly(),
                lastMonthTotal = 85.85, // Todo: Da calcolare nel ViewModel
                categories = viewModel.categoriesState.value,
                last5MonthsData = listOf( // Todo: Da prendere/calcolare dal database, questi sono solo per test
                    MonthData("Giu", 87.20),
                    MonthData("Lug", 87.20),
                    MonthData("Ago", 87.20),
                    MonthData("Set", 87.20),
                    MonthData("Ott", 87.20)
                ),
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

@Composable
private fun HandleNavigationEffects(
    navController: NavHostController,
    viewModel: SubViewModel
) {
    val currentBackStackEntry = navController.currentBackStackEntryAsState().value
    val currentScreen = currentBackStackEntry?.getCurrentScreen()

    LaunchedEffect(currentScreen) {
        if (currentScreen !is Screen.ViewSubscription) {
            viewModel.resetEditingMode()
        }
    }
}