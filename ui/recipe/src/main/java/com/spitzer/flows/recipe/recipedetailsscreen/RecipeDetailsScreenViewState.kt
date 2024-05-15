package com.spitzer.flows.recipe.recipedetailsscreen

import com.spitzer.designsystem.components.error.ErrorViewState

data class RecipeDetailsScreenViewState(
    val title: String,
    val recipeDetails: RecipeDetailsViewState? = null,
    val isLoading: Boolean = false,
    val errorViewState: ErrorViewState? = null,
    val onRefresh: () -> Unit,
    val onBackButtonPressed: () -> Unit
)
