package com.empathytraining.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmpathyNavigation(
    repository: EmpathyRepository,
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            // Handle system bars padding for edge-to-edge
            .windowInsetsPadding(WindowInsets.systemBars.only(WindowInsetsSides.Horizontal)),
        bottomBar = {
            BottomNavBar(
                navController = navController,
                // Add bottom navigation bar padding
                modifier = Modifier.windowInsetsPadding(
                    WindowInsets.systemBars.only(WindowInsetsSides.Bottom)
                )
            )
        },
        // Handle status bar
        topBar = {
            // Optional: Add a top bar that respects status bar insets
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .height(0.dp) // No visual height, just padding
            )
        }) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavigationItem.DailyChallenge.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(NavigationItem.DailyChallenge.route) {
                DailyChallengeScreen(
                    repository = repository, onNavigateToHistory = {
                        navController.navigate(NavigationItem.History.route)
                    })
            }

            composable(NavigationItem.History.route) {
                HistoryScreen(
                    repository = repository
                )
            }

            composable(NavigationItem.Progress.route) {
                ProgressScreen(
                    repository = repository, onNavigateToChallenge = {
                        navController.navigate(NavigationItem.DailyChallenge.route) {
                            popUpTo(NavigationItem.DailyChallenge.route) { inclusive = true }
                        }
                    })
            }
        }
    }
}

@Composable
private fun BottomNavBar(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar(
        modifier = modifier
    ) {
        NavigationItem.entries.forEach { item ->
            NavigationBarItem(
                icon = {
                Icon(
                    painter = painterResource(item.iconRes),
                    contentDescription = stringResource(item.titleRes)
                )
            },
                label = { Text(stringResource(item.titleRes)) },
                selected = currentDestination?.hierarchy?.any {
                    it.route == item.route
                } == true,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                })
        }
    }
}

enum class NavigationItem(
    val route: String,
    val titleRes: Int,
    val iconRes: Int,
) {
    DailyChallenge(
        route = "daily_challenge", titleRes = R.string.nav_today, iconRes = R.drawable.home
    ),
    History(
        route = "history", titleRes = R.string.nav_history, iconRes = R.drawable.history
    ),
    Progress(
        route = "progress", titleRes = R.string.nav_progress, iconRes = R.drawable.assessment
    )
}