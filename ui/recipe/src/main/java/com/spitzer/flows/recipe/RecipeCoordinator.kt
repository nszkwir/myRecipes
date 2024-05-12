package com.spitzer.flows.recipe

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.spitzer.designsystem.animations.PushTransition
import com.spitzer.designsystem.utils.getDeviceResolution
import com.spitzer.flows.recipe.InternalRoute.recipeIdKeyName
import com.spitzer.flows.recipe.recipedetailsscreen.RecipeDetailsScreen
import com.spitzer.flows.recipe.recipedetailsscreen.RecipeDetailsScreenViewModelInput
import com.spitzer.flows.recipe.recipedetailsscreen.RecipeDetailsScreenViewModelOutput
import com.spitzer.flows.recipe.recipelistscreen.RecipeListScreen
import com.spitzer.flows.recipe.recipelistscreen.RecipeListScreenViewModelOutput

const val RecipeCoordinatorRoute = "RecipeCoordinatorRoute"

internal object InternalRoute {
    const val recipeList = RecipeCoordinatorRoute + "RecipeList"
    const val recipeDetails = RecipeCoordinatorRoute + "RecipeDetails"
    const val recipeIdKeyName = "id"
    val recipeDetailsArgument = navArgument(name = recipeIdKeyName) {
        type = NavType.LongType
        defaultValue = -1
    }
}

@Composable
fun RecipeCoordinator() {
    val navHostController: NavHostController = rememberNavController()
    val deviceResolution = getDeviceResolution()
    NavHost(
        navController = navHostController,
        startDestination = InternalRoute.recipeList,
        route = RecipeCoordinatorRoute,
        enterTransition = PushTransition(deviceResolution.width).enter,
        exitTransition = PushTransition(deviceResolution.width).exit,
        popEnterTransition = PushTransition(deviceResolution.width).popEnter,
        popExitTransition = PushTransition(deviceResolution.width).popExit
    ) {
        composable(route = InternalRoute.recipeList) {
            val output: (RecipeListScreenViewModelOutput) -> Unit = {
                when (it) {
                    is RecipeListScreenViewModelOutput.RecipeDetail -> {
                        navHostController.navigate("${InternalRoute.recipeDetails}/${it.recipeId}")
                    }
                }
            }
            RecipeListScreen(
                output = output
            )
        }
        composable(
            route = "${InternalRoute.recipeDetails}/{$recipeIdKeyName}",
            arguments = listOf(InternalRoute.recipeDetailsArgument)
        ) { navBackStack ->
            val recipeId = navBackStack.arguments?.getLong(recipeIdKeyName)
            if (recipeId == null) {
                navHostController.popBackStack()
                return@composable
            }
            val input = RecipeDetailsScreenViewModelInput(
                recipeId = recipeId
            )
            val output: (RecipeDetailsScreenViewModelOutput) -> Unit = {
                when(it) {
                    RecipeDetailsScreenViewModelOutput.ScreenNavigateBack -> {
                        navHostController.popBackStack()
                    }
                }
             }
            
            RecipeDetailsScreen(
                input = input,
                output = output
            )
        }
    }
}
