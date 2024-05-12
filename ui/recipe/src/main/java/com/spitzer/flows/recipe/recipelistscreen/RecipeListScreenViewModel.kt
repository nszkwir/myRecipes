package com.spitzer.flows.recipe.recipelistscreen

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spitzer.designsystem.components.Action
import com.spitzer.designsystem.components.CardViewState
import com.spitzer.designsystem.components.error.ErrorScreen
import com.spitzer.domain.usecase.recipe.FetchNextRecipePageWhenNeededError
import com.spitzer.domain.usecase.recipe.FetchNextRecipePageWhenNeededUseCase
import com.spitzer.domain.usecase.recipe.GetRecipeListUseCase
import com.spitzer.domain.usecase.recipe.RefreshRecipeListUseCase
import com.spitzer.domain.usecase.recipe.RefreshRecipeListUseCaseError
import com.spitzer.domain.utils.WrappedResult
import com.spitzer.entity.recipe.Recipe
import com.spitzer.entity.recipe.RecipePage
import com.spitzer.entity.search.SearchCriteria
import com.spitzer.entity.search.SortCriteria
import com.spitzer.entity.search.SortOrder
import com.spitzer.recipe.R
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class RecipeListScreenViewModelOutput {
    data class RecipeDetail(val recipeId: Long) : RecipeListScreenViewModelOutput()
}

private data class RecipeListScreenViewModelDataStore(
    var selectedSearchCriteria: SearchCriteria = SearchCriteria.NAME,
    var selectedSortCriteria: SortCriteria = SortCriteria.RELEVANCE,
    var selectedSortOrder: SortOrder = SortOrder.DESCENDING,
    var hasLoadedRecipes: Boolean = false
)

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class RecipeListScreenViewModel @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
    private val getRecipeListUseCase: GetRecipeListUseCase,
    private val refreshRecipeListUseCase: RefreshRecipeListUseCase,
    private val fetchNextRecipePageWhenNeededUseCase: FetchNextRecipePageWhenNeededUseCase
) : ViewModel() {

    companion object {
        const val paginationFailedRefreshDelay = 5000L
    }

    private var dataStore = RecipeListScreenViewModelDataStore()

    lateinit var output: (RecipeListScreenViewModelOutput) -> Unit

    val viewState: StateFlow<RecipeListScreenViewState> by lazy {
        _viewState.asStateFlow()
    }

    private val _viewState by lazy {
        MutableStateFlow(
            RecipeListScreenViewState(
                title = mapTitleText(),
                onRefresh = {
                    refreshRecipeList()
                }
            )
        )
    }

    private var refreshHandler: Handler? = null

    init {
        viewModelScope.launch {
            getRecipeListUseCase()
                .collectLatest { recipePage ->
                    inputChanged(recipePage)
                }
        }

        refreshRecipeList()
    }

    private fun mapTitleText(): String {
        return applicationContext.getString(
            R.string.recipe_list_screen_title
        )
    }

    private fun inputChanged(recipePage: RecipePage) {
        _viewState.update { currentState ->
            currentState.copy(
                cardListViewStates = mapCardListViewStates(recipeList = recipePage.list),
                searchBarViewState = null,
                onPrefetchItemsAtIndex = {
                    getRecipeList(elementIndex = it)
                }
            )
        }
    }

    private fun mapCardListViewStates(recipeList: List<Recipe?>): List<CardViewState> {
        return recipeList.map { recipe ->
            recipe?.let {
                CardViewState(
                    topImageURL = it.image,
                    firstTitle = it.title,
                    secondTitle = it.summary,
                    onTap = Action(
                        action = {
                            output(RecipeListScreenViewModelOutput.RecipeDetail(it.id))
                        },
                        description = applicationContext.getString(
                            R.string.recipe_list_screen_see_card_details_content_description,
                            it.title
                        )
                    )
                )
            } ?: run {
                CardViewState(isLoading = true)
            }
        }
    }
    
    private fun refreshRecipeList() {
        fun showNoInternetConnectionError(showsCloseButton: Boolean) {
            _viewState.update { currentState ->
                currentState.copy(
                    title = applicationContext.getString(com.spitzer.designsystem.R.string.error_noInternet_title),
                    errorViewState = ErrorScreen.noInternetConnection(
                        applicationContext = applicationContext,
                        onTryAgainButtonTap = { refreshRecipeList() },
                        onCloseButtonTap = if (showsCloseButton) {
                            {
                                removeErrorView()
                            }
                        } else null
                    )
                )
            }
        }

        fun showGenericError(showsCloseButton: Boolean) {
            _viewState.update { currentState ->
                currentState.copy(
                    title = applicationContext.getString(com.spitzer.designsystem.R.string.error_generic_title),
                    errorViewState = ErrorScreen.generic(
                        applicationContext = applicationContext,
                        onTryAgainButtonTap = { refreshRecipeList() },
                        onCloseButtonTap = if (showsCloseButton) {
                            {
                                removeErrorView()
                            }
                        } else null
                    )
                )
            }
        }

        displayLoadingAnimation(true)

        viewModelScope.launch {
            val result = refreshRecipeListUseCase(
                sortCriteria = dataStore.selectedSortCriteria,
                sortOrder = dataStore.selectedSortOrder
            )
            displayLoadingAnimation(false)
            when (result) {
                is WrappedResult.Error -> {
                    if (dataStore.hasLoadedRecipes ||
                        viewState.value.cardListViewStates.isEmpty()
                    ) {
                        val showsCloseButton = dataStore.hasLoadedRecipes
                        when (result.exception) {
                            RefreshRecipeListUseCaseError.NoInternet -> {
                                showNoInternetConnectionError(showsCloseButton = showsCloseButton)
                            }

                            RefreshRecipeListUseCaseError.Generic -> {
                                showGenericError(showsCloseButton = showsCloseButton)
                            }
                        }
                    }
                }

                is WrappedResult.Success -> {
                    dataStore.hasLoadedRecipes = true
                }
            }
        }
    }

    private fun getRecipeList(elementIndex: Int) {
        fun handleRetryError() {
            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({
                getRecipeList(elementIndex = elementIndex)
            }, paginationFailedRefreshDelay)
            refreshHandler = handler
        }

        refreshHandler?.removeCallbacksAndMessages(null)
        viewModelScope.launch {
            val result = fetchNextRecipePageWhenNeededUseCase(
                elementIndex = elementIndex,
                sortCriteria = dataStore.selectedSortCriteria,
                sortOrder = dataStore.selectedSortOrder
            )
            when (result) {
                is WrappedResult.Success -> {
                    // No operation
                }

                is WrappedResult.Error -> {
                    when (result.exception) {
                        FetchNextRecipePageWhenNeededError.Retry -> {
                            handleRetryError()
                        }
                    }
                }
            }
        }
    }

    private fun displayLoadingAnimation(visible: Boolean) {
        if (visible) {
            _viewState.update { currentState ->
                currentState.copy(
                    title = mapTitleText(),
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
                title = mapTitleText(),
                errorViewState = null
            )
        }
    }
}