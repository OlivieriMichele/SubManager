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
import com.example.submanager.ui.screens.auth.LoginScreen
import com.example.submanager.ui.screens.auth.SignInScreen
import com.example.submanager.ui.screens.categories.CategoryDetailScreen
import com.example.submanager.ui.screens.categories.CategoryScreen
import com.example.submanager.ui.screens.categories.NewCategoryScreen
import com.example.submanager.ui.screens.home.HomeScreen
import com.example.submanager.ui.screens.insights.InsigthsScreen
import com.example.submanager.ui.screens.profile.ProfileScreen
import com.example.submanager.ui.screens.subscription.AddSubscriptionScreen
import com.example.submanager.ui.screens.subscription.ViewSubscriptionScreen
import com.example.submanager.ui.screens.viewModel.AuthViewModel
import com.example.submanager.ui.screens.viewModel.CategoryViewModel
import com.example.submanager.ui.screens.viewModel.HomeViewModel
import com.example.submanager.ui.screens.viewModel.InsightsViewModel
import com.example.submanager.ui.screens.viewModel.SubscriptionViewModel
import com.example.submanager.ui.screens.viewModel.ThemeViewModel
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

sealed interface Screen {
    @Serializable data object Login : Screen
    @Serializable data object Register : Screen
    @Serializable data object Home : Screen
    @Serializable data object Profile : Screen
    @Serializable data object Categories : Screen
    @Serializable data class CategoryDetail(val categoryName: String) : Screen
    @Serializable data object AddSubscription : Screen
    @Serializable data class ViewSubscription(val subscriptionId: Int) : Screen
    @Serializable data object NewCategory : Screen
    @Serializable data object Insights : Screen
}

fun NavBackStackEntry.getCurrentScreen(): Screen? {
    return destination.route?.let { route ->
        when {
            route.contains("Login") -> Screen.Login
            route.contains("Register") -> Screen.Register
            route.contains("Home") -> Screen.Home
            route.contains("Profile") -> Screen.Profile
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
    themeViewModel: ThemeViewModel,
    modifier: Modifier = Modifier
) {
    val authViewModel = koinViewModel<AuthViewModel>()
    val subscriptionViewModel = koinViewModel<SubscriptionViewModel>()
    val categoryViewModel = koinViewModel<CategoryViewModel>()

    val authState by authViewModel.state.collectAsStateWithLifecycle()
    val startDestination = if (authState.isAuthenticated) Screen.Home else Screen.Login

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Login
        composable<Screen.Login> {
            LaunchedEffect(authState.isAuthenticated) {
                if (authState.isAuthenticated) {
                    navController.navigate(Screen.Home) {
                        popUpTo(Screen.Login) { inclusive = true }
                    }
                }
            }

            LoginScreen(
                state = authState,
                actions = authViewModel.actions,
                onRegisterClick = { navController.navigate(Screen.Register) }
            )
        }

        // SignIn Screen
        composable<Screen.Register> {
            LaunchedEffect(authState.isAuthenticated) {
                if (authState.isAuthenticated) {
                    navController.navigate(Screen.Home) {
                        popUpTo(Screen.Register) { inclusive = true }
                    }
                }
            }

            SignInScreen(
                state = authState,
                actions = authViewModel.actions,
                onBackClick = { navController.popBackStack() }
            )
        }

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

        // Profile Screen
        composable<Screen.Profile> {
            val themeState by themeViewModel.state.collectAsStateWithLifecycle()

            ProfileScreen(
                authState = authState,
                authActions = authViewModel.actions,
                themeState = themeState,
                themeAction = themeViewModel.actions,
                onNotificationsToggle = { /* TODO */ },
                onLogout = {
                    authViewModel.actions.logout()
                    navController.navigate(Screen.Login) {
                        popUpTo(0) { inclusive = true }
                    }
                }
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