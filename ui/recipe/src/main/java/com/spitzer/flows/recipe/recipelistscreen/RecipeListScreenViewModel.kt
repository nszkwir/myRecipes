package com.spitzer.flows.recipe.recipelistscreen

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spitzer.designsystem.components.Action
import com.spitzer.designsystem.components.CallToActionViewState
import com.spitzer.designsystem.components.CallToActionViewStyle
import com.spitzer.designsystem.components.CardViewState
import com.spitzer.designsystem.components.PillViewState
import com.spitzer.designsystem.components.error.ErrorScreen
import com.spitzer.domain.usecase.recipe.FetchNextRecipePageWhenNeededError
import com.spitzer.domain.usecase.recipe.FetchNextRecipePageWhenNeededUseCase
import com.spitzer.domain.usecase.recipe.GetRecipeListUseCase
import com.spitzer.domain.usecase.recipe.RefreshRecipeListUseCase
import com.spitzer.domain.usecase.recipe.RefreshRecipeListUseCaseError
import com.spitzer.domain.usecase.recipe.SearchRecipePageUseCase
import com.spitzer.domain.usecase.recipe.SearchRecipePageUseCaseError
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
    private val fetchNextRecipePageWhenNeededUseCase: FetchNextRecipePageWhenNeededUseCase,
    private val searchRecipeListUseCase: SearchRecipePageUseCase
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
                searchBarViewState = mapSearchBarViewState(),
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

    private fun mapSearchBarViewState(): RecipeListScreenSearchBarViewState {
        fun removeBottomSheet() {
            _viewState.update { currentState ->
                currentState.copy(bottomSheetViewState = null)
            }
        }

        fun hideBottomSheet() {
            _viewState.update { currentState ->
                currentState.copy(
                    bottomSheetViewState = currentState.bottomSheetViewState?.copy(
                        shouldHide = true
                    )
                )
            }
        }

        fun mapIsFunnelOn(): Boolean {
            return !(dataStore.selectedSearchCriteria == SearchCriteria.NAME &&
                    dataStore.selectedSortCriteria == SortCriteria.RELEVANCE &&
                    dataStore.selectedSortOrder == SortOrder.DESCENDING)
        }

        fun inputChanged() {
            _viewState.update { currentState ->
                currentState.copy(
                    searchBarViewState = mapSearchBarViewState()
                )
            }
            refreshRecipeList()
        }

        fun showBottomSheet() {
            val temporalDataStore = dataStore.copy()

            fun clearSearchFilters() {
                dataStore.selectedSearchCriteria = SearchCriteria.NAME
                dataStore.selectedSortCriteria = SortCriteria.RELEVANCE
                dataStore.selectedSortOrder = SortOrder.DESCENDING
                hideBottomSheet()
                inputChanged()
            }

            fun confirmSearchFilters() {
                dataStore = temporalDataStore.copy()
                hideBottomSheet()
                inputChanged()
            }

            fun mapSearchCriteriaTitle(searchCriteria: SearchCriteria): String {
                return when (searchCriteria) {
                    SearchCriteria.NAME -> applicationContext.getString(R.string.search_criteria_name_title)
                    SearchCriteria.INGREDIENTS -> applicationContext.getString(R.string.search_criteria_ingredients_title)
                }
            }

            fun mapSortCriteriaTitle(sortCriteria: SortCriteria): String {
                return when (sortCriteria) {
                    SortCriteria.RELEVANCE -> applicationContext.getString(R.string.sort_criteria_relevance_title)
                    SortCriteria.POPULARITY -> applicationContext.getString(R.string.sort_criteria_popularity_title)
                    SortCriteria.PREPARATION_TIME -> applicationContext.getString(R.string.sort_criteria_preparation_time_title)
                    SortCriteria.CALORIES -> applicationContext.getString(R.string.sort_criteria_calories_title)
                }
            }

            fun mapSortOrderTitle(sortOrder: SortOrder): String {
                return when (sortOrder) {
                    SortOrder.DESCENDING -> applicationContext.getString(R.string.sort_order_descending_title)
                    SortOrder.ASCENDING -> applicationContext.getString(R.string.sort_order_ascending_title)
                }
            }

            fun mapBottomSheetViewState(): RecipeListScreenBottomSheetViewState {
                fun inputChanged() {
                    _viewState.update { currentState ->
                        currentState.copy(bottomSheetViewState = mapBottomSheetViewState())
                    }
                }

                return RecipeListScreenBottomSheetViewState(
                    funnelViewState = RecipeListScreenFunnelViewState(
                        searchCriteriaTitle = applicationContext.getString(R.string.recipe_list_screen_funnel_search_criteria_title),
                        searchCriteriaPillsViewStates = SearchCriteria.values()
                            .map { searchCriteria ->
                                PillViewState(
                                    text = mapSearchCriteriaTitle(searchCriteria),
                                    isSelected = temporalDataStore.selectedSearchCriteria == searchCriteria,
                                    onSelectionChange = {
                                        temporalDataStore.selectedSearchCriteria = searchCriteria
                                        inputChanged()
                                    }
                                )
                            },
                        sortCriteriaTitle = applicationContext.getString(R.string.recipe_list_screen_funnel_sort_criteria_title),
                        sortCriteriaPillsViewStates = SortCriteria.values().map { sortCriteria ->
                            PillViewState(
                                text = mapSortCriteriaTitle(sortCriteria),
                                isSelected = temporalDataStore.selectedSortCriteria == sortCriteria,
                                onSelectionChange = {
                                    temporalDataStore.selectedSortCriteria = sortCriteria
                                    inputChanged()
                                }
                            )
                        },
                        sortOrderTitle = applicationContext.getString(R.string.recipe_list_screen_funnel_sort_order_title),
                        sortOrderPillsViewStates = SortOrder.values().map { sortOrder ->
                            PillViewState(
                                text = mapSortOrderTitle(sortOrder),
                                isSelected = temporalDataStore.selectedSortOrder == sortOrder,
                                onSelectionChange = {
                                    temporalDataStore.selectedSortOrder = sortOrder
                                    inputChanged()
                                }
                            )
                        },
                        clearActionViewState = CallToActionViewState(
                            title = applicationContext.getString(com.spitzer.designsystem.R.string.clear_title),
                            style = CallToActionViewStyle.WARNING,
                            onTap = {
                                clearSearchFilters()
                            }
                        ),
                        confirmActionViewState = CallToActionViewState(
                            title = applicationContext.getString(com.spitzer.designsystem.R.string.confirm_title),
                            onTap = {
                                confirmSearchFilters()
                            }
                        )
                    ),
                    onDismiss = {
                        removeBottomSheet()
                    }
                )
            }

            _viewState.update { currentState ->
                currentState.copy(
                    bottomSheetViewState = mapBottomSheetViewState()
                )
            }
        }

        fun mapPlaceholderText(): String {
            return when (dataStore.selectedSearchCriteria) {
                SearchCriteria.NAME -> {
                    applicationContext.getString(R.string.recipe_list_screen_search_by_name_placeholder)
                }

                SearchCriteria.INGREDIENTS -> {
                    applicationContext.getString(R.string.recipe_list_screen_search_by_ingredients_placeholder)
                }
            }
        }

        fun openSearch() {
            _viewState.update { currentState ->
                currentState.copy(
                    searchBarViewState = currentState.searchBarViewState?.copy(
                        isSearchActive = true
                    )
                )
            }
        }

        fun closeSearch() {
            _viewState.update { currentState ->
                currentState.copy(
                    searchBarViewState = currentState.searchBarViewState?.copy(
                        isSearchActive = false
                    )
                )
            }
        }

        fun updateQuery(query: String) {
            _viewState.update { currentState ->
                currentState.copy(
                    searchBarViewState = currentState.searchBarViewState?.copy(
                        query = query
                    )
                )
            }
        }

        return RecipeListScreenSearchBarViewState(
            isSearchActive = false,
            query = "",
            placeholder = mapPlaceholderText(),
            isFunnelOn = mapIsFunnelOn(),
            onFunnelTap = {
                showBottomSheet()
            },
            onQueryChange = {
                updateQuery(it)
            },
            onSearch = {
                searchRecipeList(it)
            },
            onOpenSearch = {
                openSearch()
            },
            onCloseSearch = {
                closeSearch()
            }
        )
    }

    private fun searchRecipeList(query: String) {
        fun showNoInternetConnectionError() {
            _viewState.update { currentState ->
                currentState.copy(
                    title = applicationContext.getString(com.spitzer.designsystem.R.string.error_noInternet_title),
                    errorViewState = ErrorScreen.noInternetConnection(
                        applicationContext = applicationContext,
                        onTryAgainButtonTap = { searchRecipeList(query = query) },
                        onCloseButtonTap = {
                            removeErrorView()
                        }
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
                        onTryAgainButtonTap = { searchRecipeList(query = query) },
                        onCloseButtonTap = {
                            removeErrorView()
                        }
                    )
                )
            }
        }

        fun showSearchResults(list: List<Recipe>) {
            _viewState.update { currentState ->
                currentState.copy(
                    searchBarViewState = currentState.searchBarViewState?.copy(
                        cardListViewStates = mapCardListViewStates(list)
                    )
                )
            }
        }

        displayLoadingAnimation(true)

        viewModelScope.launch {
            val result = searchRecipeListUseCase(
                query = query,
                searchCriteria = dataStore.selectedSearchCriteria,
                sortCriteria = dataStore.selectedSortCriteria,
                sortOrder = dataStore.selectedSortOrder
            )
            when (result) {
                is WrappedResult.Error -> {
                    when (result.exception) {
                        SearchRecipePageUseCaseError.NoInternet -> {
                            showNoInternetConnectionError()
                        }

                        SearchRecipePageUseCaseError.Generic -> {
                            showGenericError()
                        }
                    }
                }

                is WrappedResult.Success -> {
                    showSearchResults(result.data)
                }
            }
            displayLoadingAnimation(false)
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