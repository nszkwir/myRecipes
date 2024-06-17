package com.spitzer.flows.recipe.recipelistscreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import com.spitzer.designsystem.R.raw.funnel_off
import com.spitzer.designsystem.R.raw.funnel_on
import com.spitzer.designsystem.R.raw.search_not_found
import com.spitzer.designsystem.animations.LottieAnimationView
import com.spitzer.designsystem.components.CallToActionView
import com.spitzer.designsystem.components.CardView
import com.spitzer.designsystem.components.CardViewState
import com.spitzer.designsystem.components.LoadingView
import com.spitzer.designsystem.components.PillView
import com.spitzer.designsystem.components.PillViewState
import com.spitzer.designsystem.components.error.ErrorScreenView
import com.spitzer.designsystem.theme.RTheme
import com.spitzer.designsystem.theme.Spacing
import com.spitzer.recipe.R
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RecipeListScreenView(
    viewState: RecipeListScreenViewState, modifier: Modifier = Modifier
) {
    val refreshing by remember { mutableStateOf(false) }
    val state = rememberPullRefreshState(refreshing, { viewState.onRefresh() })
    Box(
        modifier = modifier
            .pullRefresh(state)
            .fillMaxSize()
            .background(RTheme.colors.n99n00)
    ) {
        if (viewState.isLoading) {
            LoadingView()
        } else if (viewState.errorViewState == null) {
            Column(
                verticalArrangement = Arrangement.spacedBy(Spacing.TWO.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                viewState.searchBarViewState?.let { searchBarViewState ->
                    SearchBarView(viewState = searchBarViewState)
                }

                if (viewState.cardListViewStates.isNotEmpty()) {
                    CardListView(
                        cardListViewStates = viewState.cardListViewStates,
                        onPrefetchItemsAtIndex = viewState.onPrefetchItemsAtIndex
                    )
                } else {
                    EmptySearchView()
                }
            }
            PullRefreshIndicator(refreshing, state, Modifier.align(Alignment.TopCenter))
        }

        AnimatedVisibility(
            visible = viewState.errorViewState != null, enter = fadeIn(), exit = fadeOut()
        ) {
            viewState.errorViewState?.let { viewState ->
                ErrorScreenView(
                    viewState = viewState
                )
            }
        }

        viewState.bottomSheetViewState?.let { viewState ->
            FilterSortView(viewState = viewState)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterSortView(viewState: RecipeListScreenBottomSheetViewState) {
    val density = LocalDensity.current
    var isHiding by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val funnelViewState = viewState.funnelViewState

    LaunchedEffect(viewState.shouldHide) {
        if (viewState.shouldHide) {
            isHiding = true
            sheetState.hide()
        }
    }

    if (sheetState.currentValue == SheetValue.Hidden && sheetState.targetValue == SheetValue.Hidden && isHiding) {
        viewState.onDismiss()
    }

    ModalBottomSheet(
        onDismissRequest = {
            viewState.onDismiss()
        },
        sheetState = sheetState
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(Spacing.TWO.dp),
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.TWO.dp)
                .padding(bottom = Spacing.TWO.dp)
        ) {
            FunnelBlockView(
                title = funnelViewState.searchCriteriaTitle,
                pillsViewStates = funnelViewState.searchCriteriaPillsViewStates
            )
            FunnelBlockView(
                title = funnelViewState.sortCriteriaTitle,
                pillsViewStates = funnelViewState.sortCriteriaPillsViewStates
            )
            FunnelBlockView(
                title = funnelViewState.sortOrderTitle,
                pillsViewStates = funnelViewState.sortOrderPillsViewStates
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(
                    Spacing.TWO.dp,
                    alignment = Alignment.CenterHorizontally
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.TWO.dp)
            ) {
                var buttonMinWidth by remember { mutableStateOf(0.dp) }
                CallToActionView(
                    viewState = funnelViewState.clearActionViewState,
                    modifier = Modifier
                        .onGloballyPositioned {
                            val width = with(density) {
                                it.size.width.toDp()
                            }
                            buttonMinWidth = max(width, buttonMinWidth)
                        }
                        .defaultMinSize(minWidth = buttonMinWidth)
                )

                CallToActionView(
                    viewState = funnelViewState.confirmActionViewState,
                    modifier = Modifier
                        .onGloballyPositioned {
                            val width = with(density) {
                                it.size.width.toDp()
                            }
                            buttonMinWidth = max(width, buttonMinWidth)
                        }
                        .defaultMinSize(minWidth = buttonMinWidth)
                )
            }
        }
    }
}

@Composable
private fun FunnelBlockView(
    title: String,
    pillsViewStates: List<PillViewState>,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(Spacing.ONE.dp)
    ) {
        Text(
            text = title,
            color = RTheme.colors.n00n00,
            style = RTheme.typography.heading3,
            fontWeight = FontWeight.Bold
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(Spacing.TWO.dp)
        ) {
            items(pillsViewStates.count()) { index ->
                PillView(viewState = pillsViewStates[index])
            }
        }
    }
}

@Composable
private fun CardListView(
    cardListViewStates: List<CardViewState>,
    onPrefetchItemsAtIndex: ((Int) -> Unit)?,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        Column {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .padding(horizontal = Spacing.TWO.dp)
                    .background(Color.Transparent)
                    .testTag(stringResource(id = R.string.recipeList_TestTag))
            ) {
                items(cardListViewStates.size) { index ->
                    CardView(
                        viewState = cardListViewStates[index],
                        modifier = Modifier
                            .padding(bottom = Spacing.TWO.dp)
                    )
                }
            }
        }

        // Observe scroll state to load more items
        LaunchedEffect(listState) {
            snapshotFlow { listState.layoutInfo.visibleItemsInfo }.collectLatest {
                val lastItem = it.last()
                onPrefetchItemsAtIndex?.invoke(lastItem.index)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBarView(viewState: RecipeListScreenSearchBarViewState) {
    SearchBar(
        query = viewState.query,
        onQueryChange = {
            viewState.onQueryChange(it)
        }, onSearch = {
            viewState.onSearch(it)
        },
        active = viewState.isSearchActive,
        onActiveChange = {
            viewState.onOpenSearch()
        },
        placeholder = {
            Text(
                text = viewState.placeholder,
                color = RTheme.colors.n00n00,
                style = RTheme.typography.body1
            )
        },
        leadingIcon = {
            if (viewState.isSearchActive) {
                Image(
                    painter = painterResource(id = com.spitzer.designsystem.R.drawable.close),
                    contentDescription = stringResource(id = R.string.recipe_list_screen_search_close_content_description),
                    modifier = Modifier
                        .clickable(indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                            onClick = {
                                viewState.onCloseSearch()
                            })
                )
            } else {
                Image(
                    painter = painterResource(id = com.spitzer.designsystem.R.drawable.search),
                    contentDescription = stringResource(id = R.string.recipe_list_screen_search_content_description)
                )
            }
        },
        trailingIcon = {
            LottieAnimationView(
                animation = if (viewState.isFunnelOn) funnel_on else funnel_off,
                shouldLoop = true,
                modifier = Modifier
                    .size(width = 48.dp, height = 48.dp)
                    .clickable(indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = {
                            viewState.onFunnelTap()
                        })
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.TWO.dp)
    ) {
        if (viewState.cardListViewStates.isEmpty()) {
            EmptySearchView()
        } else {
            CardListView(
                cardListViewStates = viewState.cardListViewStates,
                onPrefetchItemsAtIndex = null,
                modifier = Modifier.padding(top = Spacing.TWO.dp)
            )
        }
    }
}

@Composable
private fun EmptySearchView() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Column {
            LottieAnimationView(
                animation = search_not_found, modifier = Modifier.height(250.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}
