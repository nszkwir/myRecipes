package com.spitzer.recipes.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.spitzer.flows.recipe.RecipeCoordinator
import com.spitzer.flows.recipe.RecipeCoordinatorRoute
import com.spitzer.recipes.R

const val MainCoordinatorRoute = "MainCoordinatorRoute"

internal object InternalRoute {
    const val home = MainCoordinatorRoute + "Home"
}

sealed class MainCoordinatorOutput {
    data class DarkModeToggle(val enabled: Boolean) : MainCoordinatorOutput()
}

@Composable
fun MainCoordinator() {

    val rootNavController: NavHostController = rememberNavController()

    val navItems: List<BottomNavItem> = listOf(
        BottomNavItem(RecipeCoordinatorRoute, R.string.bottom_nav_bar_recipes, R.drawable.dish)
    )

    NavHost(
        navController = rootNavController,
        startDestination = InternalRoute.home,
        route = MainCoordinatorRoute
    ) {
        composable(route = InternalRoute.home) {
            val navHostController: NavHostController = rememberNavController()

            Scaffold(
                bottomBar = {
                    BottomNavBar(
                        screens = navItems,
                        navController = navHostController
                    )
                }
            ) { innerPadding ->
                NavHost(
                    navController = navHostController,
                    startDestination = navItems.first().route,
                    modifier = Modifier.padding(innerPadding)
                ) {
                    composable(route = RecipeCoordinatorRoute) {
                        RecipeCoordinator()
                    }
                }
            }
        }
    }
}
