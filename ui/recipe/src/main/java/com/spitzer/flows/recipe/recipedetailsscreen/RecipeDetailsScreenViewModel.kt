package com.spitzer.flows.recipe.recipedetailsscreen

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spitzer.designsystem.components.Action
import com.spitzer.designsystem.components.error.ErrorScreen
import com.spitzer.domain.usecase.favorites.SetRecipeFavoriteStatusUseCase
import com.spitzer.domain.usecase.recipedetails.GetRecipeDetailsByIdUseCase
import com.spitzer.domain.usecase.recipedetails.GetRecipeDetailsByIdUseCaseError
import com.spitzer.domain.usecase.recipedetails.RefreshRecipeDetailsByIdUseCase
import com.spitzer.domain.usecase.recipedetails.RefreshRecipeDetailsByIdUseCaseError
import com.spitzer.domain.utils.WrappedResult
import com.spitzer.entity.recipe.RecipeDetails
import com.spitzer.utils.ImmutableVariable
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RecipeDetailsScreenViewModelInput(
    val recipeId: Long
)

sealed class RecipeDetailsScreenViewModelOutput {
    data object ScreenNavigateBack : RecipeDetailsScreenViewModelOutput()
}

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class RecipeDetailsScreenViewModel @Inject constructor(
    @ApplicationContext val applicationContext: Context,
    private val getRecipeByIdUseCase: GetRecipeDetailsByIdUseCase,
    private val refreshRecipeDetailsByIdUseCase: RefreshRecipeDetailsByIdUseCase,
    private val setRecipeFavoriteStatusUseCase: SetRecipeFavoriteStatusUseCase
) : ViewModel() {

    val viewState: StateFlow<RecipeDetailsScreenViewState> by lazy {
        _viewState.asStateFlow()
    }

    var input: RecipeDetailsScreenViewModelInput by ImmutableVariable {
        loadRecipeDetails()
    }

    lateinit var output: (RecipeDetailsScreenViewModelOutput) -> Unit

    private val _viewState by lazy {
        MutableStateFlow(
            RecipeDetailsScreenViewState(
                title = "",
                onRefresh = {
                    refreshRecipeDetails()
                },
                onBackButtonPressed = {
                    output(RecipeDetailsScreenViewModelOutput.ScreenNavigateBack)
                }
            )
        )
    }

    private fun inputChanged(recipeDetails: RecipeDetails) {

        fun recipeFavoriteStatusChanged() {
            val isFavorite = !(_viewState.value.recipeDetails?.isFavorite ?: true)

            viewModelScope.launch {
                val result =
                    setRecipeFavoriteStatusUseCase(id = input.recipeId, isFavorite = isFavorite)
                when (result) {
                    is WrappedResult.Success -> {
                        _viewState.update { currentState ->
                            currentState.copy(
                                recipeDetails = currentState.recipeDetails?.copy(
                                    isFavorite = isFavorite
                                )
                            )
                        }
                    }

                    else -> {}
                }
            }
        }

        removeErrorView()

        _viewState.update { currentState ->
            currentState.copy(
                recipeDetails = RecipeDetailsViewState(
                    imageUrl = recipeDetails.image,
                    title = recipeDetails.title,
                    summary = recipeDetails.summary,
                    instructions = recipeDetails.instructions,
                    isFavorite = recipeDetails.isFavorite,
                    onFavoriteTapped = Action(
                        action = {
                            recipeFavoriteStatusChanged()
                        },
                        description = null
                    ),
                    readyInMinutes = recipeDetails.readyInMinutes,
                    servings = recipeDetails.servings,
                    calories = recipeDetails.healthScore,
                    vegan = recipeDetails.vegan,
                    spoonacularScore = recipeDetails.spoonacularScore,
                    spoonacularSourceUrl = recipeDetails.spoonacularSourceUrl,
                    ingredients = recipeDetails.ingredients,
                    isLoading = false,
                )
            )
        }
    }

    private fun loadRecipeDetails() {
        fun showNoInternetConnectionError() {
            _viewState.update { currentState ->
                currentState.copy(
                    title = applicationContext.getString(com.spitzer.designsystem.R.string.error_noInternet_subtitle),
                    errorViewState = ErrorScreen.noInternetConnection(
                        applicationContext = applicationContext,
                        onTryAgainButtonTap = { loadRecipeDetails() },
                        onCloseButtonTap = { output(RecipeDetailsScreenViewModelOutput.ScreenNavigateBack) }
                    )
                )
            }
        }

        fun showGenericError() {
            _viewState.update { currentState ->
                currentState.copy(
                    title = applicationContext.getString(com.spitzer.designsystem.R.string.error_generic_title),
                    errorViewState = ErrorScreen.generic(
                        applicationContext = applicationContext,
                        onTryAgainButtonTap = { loadRecipeDetails() },
                        onCloseButtonTap = { output(RecipeDetailsScreenViewModelOutput.ScreenNavigateBack) }
                    )
                )
            }
        }

        displayLoadingAnimation(true)

        viewModelScope.launch {
            when (val result = getRecipeByIdUseCase(input.recipeId)) {
                is WrappedResult.Error -> {
                    when (result.exception) {
                        GetRecipeDetailsByIdUseCaseError.NoInternet -> {
                            showNoInternetConnectionError()
                        }

                        GetRecipeDetailsByIdUseCaseError.Generic -> {
                            showGenericError()
                        }
                    }
                }

                is WrappedResult.Success -> {
                    inputChanged(recipeDetails = result.data)
                }
            }

            displayLoadingAnimation(false)
        }
    }

    private fun refreshRecipeDetails() {
        fun showNoInternetConnectionError() {
            _viewState.update { currentState ->
                currentState.copy(
                    title = applicationContext.getString(com.spitzer.designsystem.R.string.error_noInternet_title),
                    errorViewState = ErrorScreen.noInternetConnection(
                        applicationContext = applicationContext,
                        onTryAgainButtonTap = { refreshRecipeDetails() },
                        onCloseButtonTap = { output(RecipeDetailsScreenViewModelOutput.ScreenNavigateBack) }
                    )
                )
            }
        }

        fun showGenericError() {
            _viewState.update { currentState ->
                currentState.copy(
                    title = applicationContext.getString(com.spitzer.designsystem.R.string.error_generic_title),
                    errorViewState = ErrorScreen.generic(
                        applicationContext = applicationContext,
                        onTryAgainButtonTap = { refreshRecipeDetails() },
                        onCloseButtonTap = { output(RecipeDetailsScreenViewModelOutput.ScreenNavigateBack) }
                    )
                )
            }
        }

        displayLoadingAnimation(true)

        viewModelScope.launch {
            when (val result = refreshRecipeDetailsByIdUseCase(input.recipeId)) {
                is WrappedResult.Error -> {
                    when (result.exception) {
                        RefreshRecipeDetailsByIdUseCaseError.NoInternet -> {
                            showNoInternetConnectionError()
                        }

                        RefreshRecipeDetailsByIdUseCaseError.Generic -> {
                            showGenericError()
                        }
                    }
                }

                is WrappedResult.Success -> {
                    inputChanged(
                        recipeDetails = result.data,
                    )
                }
            }

            displayLoadingAnimation(false)
        }
    }

    private fun displayLoadingAnimation(visible: Boolean) {
        if (visible) {
            _viewState.update { currentState ->
                currentState.copy(
                    title = "",
                    errorViewState = null,
                    isLoading = true
                )
            }
        } else {
            _viewState.update { currentState ->
                currentState.copy(
                    isLoading = false
                )
            }
        }
    }

    private fun removeErrorView() {
        _viewState.update { currentState ->
            currentState.copy(
                title = "",
                errorViewState = null
            )
        }
    }
}
