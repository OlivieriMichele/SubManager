package com.example.submanager.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.toRoute
import com.example.submanager.model.MonthData
import com.example.submanager.ui.screens.categories.CategoryDetailScreen
import com.example.submanager.ui.screens.categories.CategoryScreen
import com.example.submanager.ui.screens.categories.CategoryViewModel
import com.example.submanager.ui.screens.categories.NewCategoryScreen
import com.example.submanager.ui.screens.home.HomeScreen
import com.example.submanager.ui.screens.home.HomeViewModel
import com.example.submanager.ui.screens.insights.InsigthsScreen
import com.example.submanager.ui.screens.subscription.AddSubscriptionScreen
import com.example.submanager.ui.screens.subscription.SubscriptionViewModel
import com.example.submanager.ui.screens.subscription.ViewSubscriptionScreen
import com.example.submanager.viewModel.SubViewModel
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

// Definizione type-safe delle route
sealed interface Screen {
    @Serializable data object Home : Screen
    @Serializable data object Categories : Screen
    @Serializable data class CategoryDetail(val categoryName: String) : Screen
    @Serializable data object AddSubscription : Screen
    @Serializable data class ViewSubscription(val subscriptionId: Int) : Screen
    @Serializable data object NewCategory : Screen
    @Serializable data object Insights : Screen
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
    viewModel: SubViewModel, // Todo: temporaneo, da rimuovere a finre refactory
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
            val homeViewModel = koinViewModel<HomeViewModel>()
            val homeState by homeViewModel.state.collectAsStateWithLifecycle()

            HomeScreen(
                state = homeState,
                actions = homeViewModel.actions,
                onNavigateToCategories = { navController.navigate(Screen.Categories) },
                onSubscriptionClick = { subscriptionId ->
                    navController.navigate(Screen.ViewSubscription(subscriptionId))
                },
                onNavigateToInsights = { navController.navigate(Screen.Insights) }
            )
        }

        // Categories Screen
        composable<Screen.Categories> {
            val viewModel = koinViewModel<CategoryViewModel>()
            val state by viewModel.categoryListState.collectAsStateWithLifecycle()

            CategoryScreen(
                state = state,
                onCategoryClick = { categoryName ->
                    navController.navigate(Screen.CategoryDetail(categoryName))
                }
            )
        }

        // Category Detail Screen
        composable<Screen.CategoryDetail> { backStackEntry ->
            val route = backStackEntry.toRoute<Screen.CategoryDetail>()
            val viewModel = koinViewModel<CategoryViewModel>()
            val state by viewModel.categoryDetailState.collectAsStateWithLifecycle()

            LaunchedEffect(route.categoryName) {
                viewModel.actions.loadCategoryDetail(route.categoryName)
            }

            CategoryDetailScreen(
                state = state,
                onSubscriptionClick = {id -> navController.navigate(Screen.ViewSubscription(id))}
            )
        }

        // Add Subscription Screen
        composable<Screen.AddSubscription> {
            val viewModel = koinViewModel<SubscriptionViewModel>()
            val state by viewModel.state.collectAsStateWithLifecycle()

            AddSubscriptionScreen(
                state = state,
                actions = viewModel.actions
            )
        }

        // View Subscription Screen
        composable<Screen.ViewSubscription> { backStackEntry ->
            val route = backStackEntry.toRoute<Screen.ViewSubscription>()
            val viewModel = koinViewModel<SubscriptionViewModel>()
            val state by viewModel.state.collectAsStateWithLifecycle()

            // Carica la subscription quando entri nella schermata
            LaunchedEffect(route.subscriptionId) {
                viewModel.actions.loadSubscription(route.subscriptionId)
            }

            ViewSubscriptionScreen(
                state = state,
                actions = viewModel.actions
            )
        }

        // New Category Screen
        composable<Screen.NewCategory> {
            val viewModel = koinViewModel<CategoryViewModel>()
            val state by viewModel.categoryFormState.collectAsStateWithLifecycle()

            // Todo: sposta in un oggetto condiviso magari dinamico
            val availableIcons = listOf(
                Icons.Default.ShoppingCart,
                Icons.Default.Build,
                Icons.Default.Favorite,
                Icons.Default.Email,
                Icons.Default.DateRange,
                Icons.Default.Face,
                Icons.Default.Home
            )

            NewCategoryScreen(
                state = state,
                actions = viewModel.actions,
                onSave = { selectedIcon ->
                    val icon = availableIcons[state.selectedIconIndex]
                    viewModel.saveCategoryWithIcon(icon) {
                        navController.popBackStack()
                    }
                }
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
                )
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