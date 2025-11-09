package com.example.submanager.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.submanager.ui.screens.categories.CategoryDetailScreen
import com.example.submanager.ui.screens.categories.CategoryScreen
import com.example.submanager.ui.screens.viewModel.CategoryViewModel
import com.example.submanager.ui.screens.categories.NewCategoryScreen
import com.example.submanager.ui.screens.home.HomeScreen
import com.example.submanager.ui.screens.viewModel.HomeViewModel
import com.example.submanager.ui.screens.viewModel.InsightsViewModel
import com.example.submanager.ui.screens.insights.InsigthsScreen
import com.example.submanager.ui.screens.subscription.AddSubscriptionScreen
import com.example.submanager.ui.screens.viewModel.SubscriptionViewModel
import com.example.submanager.ui.screens.subscription.ViewSubscriptionScreen
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

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
    modifier: Modifier
) {
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
            val categoryViewModel = koinViewModel<CategoryViewModel>()
            val state by categoryViewModel.categoryListState.collectAsStateWithLifecycle()

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
            val categoryDetailViewModel = koinViewModel<CategoryViewModel>()
            val state by categoryDetailViewModel.categoryDetailState.collectAsStateWithLifecycle()

            LaunchedEffect(route.categoryName) {
                categoryDetailViewModel.actions.loadCategoryDetail(route.categoryName)
            }

            CategoryDetailScreen(
                state = state,
                onSubscriptionClick = {id -> navController.navigate(Screen.ViewSubscription(id))}
            )
        }

        // Add Subscription Screen
        composable<Screen.AddSubscription> {
            val addViewModel = koinViewModel<SubscriptionViewModel>()
            val state by addViewModel.state.collectAsStateWithLifecycle()

            AddSubscriptionScreen(
                state = state,
                actions = addViewModel.actions
            )
        }

        // View Subscription Screen
        composable<Screen.ViewSubscription> { backStackEntry ->
            val route = backStackEntry.toRoute<Screen.ViewSubscription>()
            val viewViewModel = koinViewModel<SubscriptionViewModel>()
            val state by viewViewModel.state.collectAsStateWithLifecycle()

            // Carica la subscription quando entri nella schermata
            LaunchedEffect(route.subscriptionId) {
                viewViewModel.actions.loadSubscription(route.subscriptionId)
            }

            ViewSubscriptionScreen(
                state = state,
                actions = viewViewModel.actions
            )
        }

        // New Category Screen
        composable<Screen.NewCategory> {
            val newViewModel = koinViewModel<CategoryViewModel>()
            val state by newViewModel.categoryFormState.collectAsStateWithLifecycle()

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
                actions = newViewModel.actions,
                onSave = {
                    val icon = availableIcons[state.selectedIconIndex]
                    newViewModel.saveCategoryWithIcon(icon) {
                        navController.popBackStack()
                    }
                }
            )
        }

        // Insights Screen
        composable<Screen.Insights> {
            val insightsViewModel = koinViewModel<InsightsViewModel>()
            val state by insightsViewModel.state.collectAsStateWithLifecycle()
            InsigthsScreen(state = state)
        }
    }
}