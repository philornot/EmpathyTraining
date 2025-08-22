package com.empathytraining.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.empathytraining.R
import com.empathytraining.data.repository.EmpathyRepository
import com.empathytraining.ui.screens.DailyChallengeScreen
import com.empathytraining.ui.screens.HistoryScreen
import com.empathytraining.ui.screens.ProgressScreen

import timber.log.Timber

/**
 * Main navigation setup for Empathy Training app Uses Jetpack Compose
 * Navigation with bottom navigation bar
 *
 * The app has three main sections:
 * 1. Daily Challenge - main screen for empathy practice
 * 2. History - view past responses and progress
 * 3. Progress - statistics and achievements
 *
 * @param repository Repository for data access across all screens
 * @param modifier Modifier for styling the navigation component
 */
@Composable
fun EmpathyNavigation(
    repository: EmpathyRepository,
    modifier: Modifier = Modifier,
) {
    Timber.d("Setting up main navigation")

    // Create navigation controller for managing navigation state
    val navController = rememberNavController()

    // Setup main navigation structure with bottom navigation
    Scaffold(
        modifier = modifier, bottomBar = {
            EmpathyBottomNavigationBar(navController = navController)
        }) { innerPadding ->
        // Main navigation host that handles screen transitions
        EmpathyNavHost(
            navController = navController,
            repository = repository,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

/**
 * Bottom navigation bar with three main tabs Uses Material 3 NavigationBar
 * for modern design
 *
 * @param navController Navigation controller for handling tab switches
 */
@Composable
private fun EmpathyBottomNavigationBar(
    navController: NavHostController,
) {
    // Get current navigation state to highlight active tab
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Timber.d("Current destination: ${currentDestination?.route}")

    NavigationBar {
        // Iterate through all navigation items and create tabs
        NavigationItem.entries.forEach { item ->
            NavigationBarItem(icon = {
                Icon(
                    painter = painterResource(item.iconRes), contentDescription = item.title
                )
            }, label = {
                Text(text = item.title)
            }, selected = currentDestination?.hierarchy?.any {
                it.route == item.route
            } == true, onClick = {
                Timber.d("Navigating to ${item.route}")

                navController.navigate(item.route) {
                    // Pop up to the start destination of the graph to
                    // avoid building up a large stack of destinations
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    // Avoid multiple copies of the same destination when
                    // reselecting the same item
                    launchSingleTop = true
                    // Restore state when reselecting a previously selected item
                    restoreState = true
                }
            })
        }
    }
}

/**
 * Main navigation host that defines all app screens and their routes
 * Handles the actual screen composition and data passing
 *
 * @param navController Navigation controller for screen transitions
 * @param repository Repository passed to all screens for data access
 * @param modifier Modifier for styling
 */
@Composable
private fun EmpathyNavHost(
    navController: NavHostController,
    repository: EmpathyRepository,
    modifier: Modifier = Modifier,
) {
    Timber.d("Setting up NavHost with repository")

    NavHost(
        navController = navController,
        startDestination = NavigationItem.DailyChallenge.route,
        modifier = modifier
    ) {
        // Daily Challenge Screen - main empathy practice screen
        composable(NavigationItem.DailyChallenge.route) {
            Timber.d("Composing DailyChallengeScreen")
            DailyChallengeScreen(
                repository = repository, onNavigateToHistory = {
                    Timber.d("Navigating to History from DailyChallenge")
                    navController.navigate(NavigationItem.History.route)
                })
        }

        // History Screen - view past responses and scenarios
        composable(NavigationItem.History.route) {
            Timber.d("Composing HistoryScreen")
            HistoryScreen(
                repository = repository, onNavigateBack = {
                    Timber.d("Navigating back from History")
                    navController.popBackStack()
                })
        }

        // Progress Screen - statistics, streaks, and achievements
        composable(NavigationItem.Progress.route) {
            Timber.tag("EmpathyNavigation").d("Composing ProgressScreen")
            ProgressScreen(
                repository = repository, onNavigateToChallenge = {
                    Timber.tag("EmpathyNavigation").d("Navigating to Challenge from Progress")
                    navController.navigate(NavigationItem.DailyChallenge.route) {
                        popUpTo(NavigationItem.DailyChallenge.route) {
                            inclusive = true
                        }
                    }
                })
        }
    }
}

/**
 * Enum defining all navigation destinations in the app Each item contains
 * route, title, and icon for bottom navigation
 *
 * This centralized approach makes it easy to:
 * - Add new destinations
 * - Change routes or titles
 * - Maintain consistency across navigation
 */
enum class NavigationItem(
    val route: String,
    val title: String,
    val iconRes: Int,
) {
    /**
     * Daily Challenge - Main screen where users practice empathy Shows today's
     * scenario and allows response submission
     */
    DailyChallenge(
        route = "daily_challenge", title = "Today", iconRes =  R.drawable.home
    ),

    /**
     * History - Shows past responses and allows review Users can see their
     * previous empathetic responses and examples
     */
    History(
        route = "history", title = "History", iconRes = R.drawable.history
    ),

    /**
     * Progress - Statistics, streaks, and achievements Shows user's progress
     * over time and motivational elements
     */
    Progress(
        route = "progress", title = "Progress", iconRes =  R.drawable.assessment
    );

    companion object {
        /**
         * Get all navigation items as a list Useful for iterating through all
         * destinations
         */
        fun values(): Array<NavigationItem> = enumValues()

        /**
         * Find navigation item by route Useful for programmatic navigation or
         * validation
         */
        fun findByRoute(route: String): NavigationItem? {
            return values().find { it.route == route }
        }
    }
}