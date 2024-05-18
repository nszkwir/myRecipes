package com.spitzer.settings

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.spitzer.settings.settingsscreen.SettingsScreen
import com.spitzer.settings.settingsscreen.SettingsScreenViewModelOutput

const val SettingsCoordinatorRoute = "SettingsCoordinatorRoute"

internal object InternalRoute {
    const val settings = SettingsCoordinatorRoute + "Settings"
}

sealed class SettingsCoordinatorOutput {
    data class DarkModeToggle(val enabled: Boolean) : SettingsCoordinatorOutput()
}

@Composable
fun SettingsCoordinator(
    coordinatorOutput: (SettingsCoordinatorOutput) -> Unit
) {
    val navHostController: NavHostController = rememberNavController()
    NavHost(
        navController = navHostController,
        startDestination = InternalRoute.settings,
        route = SettingsCoordinatorRoute
    ) {
        composable(route = InternalRoute.settings) {
            val output: (SettingsScreenViewModelOutput) -> Unit = {
                when(it) {
                    is SettingsScreenViewModelOutput.DarkModeToggle -> {
                        coordinatorOutput(SettingsCoordinatorOutput.DarkModeToggle(it.enabled))
                    }
                }
            }
            SettingsScreen(output = output)
        }
    }
}
