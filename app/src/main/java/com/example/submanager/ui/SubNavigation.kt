package com.example.submanager.ui

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
import com.example.submanager.ui.screens.categories.NewCategoryScreen
import com.example.submanager.ui.screens.home.HomeScreen
import com.example.submanager.ui.screens.insights.InsigthsScreen
import com.example.submanager.ui.screens.subscription.AddSubscriptionScreen
import com.example.submanager.ui.screens.subscription.ViewSubscriptionScreen
import com.example.submanager.ui.screens.viewModel.CategoryViewModel
import com.example.submanager.ui.screens.viewModel.HomeViewModel
import com.example.submanager.ui.screens.viewModel.InsightsViewModel
import com.example.submanager.ui.screens.viewModel.SubscriptionViewModel
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
    subscriptionViewModel: SubscriptionViewModel,
    categoryViewModel: CategoryViewModel,
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
            val state by subscriptionViewModel.state.collectAsStateWithLifecycle()

            AddSubscriptionScreen(
                state = state,
                actions = subscriptionViewModel.actions
            )
        }

        // View Subscription Screen
        composable<Screen.ViewSubscription> { backStackEntry ->
            val route = backStackEntry.toRoute<Screen.ViewSubscription>()
            val state by subscriptionViewModel.state.collectAsStateWithLifecycle()

            // Carica la subscription quando entri nella schermata
            LaunchedEffect(route.subscriptionId) {
                subscriptionViewModel.actions.loadSubscription(route.subscriptionId)
            }

            ViewSubscriptionScreen(
                state = state,
                actions = subscriptionViewModel.actions
            )
        }

        // New Category Screen
        composable<Screen.NewCategory> {
            val state by categoryViewModel.categoryFormState.collectAsStateWithLifecycle()
            NewCategoryScreen(state = state, actions = categoryViewModel.actions)
        }

        // Insights Screen
        composable<Screen.Insights> {
            val insightsViewModel = koinViewModel<InsightsViewModel>()
            val state by insightsViewModel.state.collectAsStateWithLifecycle()
            InsigthsScreen(state = state)
        }
    }
}