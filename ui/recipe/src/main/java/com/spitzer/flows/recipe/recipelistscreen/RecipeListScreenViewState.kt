package com.spitzer.flows.recipe.recipelistscreen

import com.spitzer.designsystem.components.CallToActionViewState
import com.spitzer.designsystem.components.CardViewState
import com.spitzer.designsystem.components.PillViewState
import com.spitzer.designsystem.components.error.ErrorViewState

data class RecipeListScreenViewState(
    val title: String,
    val cardListViewStates: List<CardViewState> = emptyList(),
    val isLoading: Boolean = false,
    val searchBarViewState: RecipeListScreenSearchBarViewState? = null,
    val bottomSheetViewState: RecipeListScreenBottomSheetViewState? = null,
    val errorViewState: ErrorViewState? = null,
    val onPrefetchItemsAtIndex: ((Int) -> Unit)? = null,
    val onRefresh: () -> Unit
)

data class RecipeListScreenBottomSheetViewState(
    val funnelViewState: RecipeListScreenFunnelViewState,
    val shouldHide: Boolean = false,
    val onDismiss: () -> Unit
)

data class RecipeListScreenFunnelViewState(
    val searchCriteriaTitle: String,
    val searchCriteriaPillsViewStates: List<PillViewState>,
    val sortCriteriaTitle: String,
    val sortCriteriaPillsViewStates: List<PillViewState>,
    val sortOrderTitle: String,
    val sortOrderPillsViewStates: List<PillViewState>,
    val clearActionViewState: CallToActionViewState,
    val confirmActionViewState: CallToActionViewState
)

data class RecipeListScreenSearchBarViewState(
    val isSearchActive: Boolean,
    val query: String,
    val placeholder: String,
    val isFunnelOn: Boolean,
    val cardListViewStates: List<CardViewState> = emptyList(),
    val onQueryChange: (String) -> Unit,
    val onFunnelTap: () -> Unit,
    val onSearch: (String) -> Unit,
    val onOpenSearch: () -> Unit,
    val onCloseSearch: () -> Unit
)