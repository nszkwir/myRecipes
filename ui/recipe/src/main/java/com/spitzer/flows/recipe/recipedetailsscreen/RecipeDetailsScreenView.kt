package com.spitzer.flows.recipe.recipedetailsscreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.spitzer.designsystem.R
import com.spitzer.designsystem.animations.LottieAnimationView
import com.spitzer.designsystem.components.LoadingView
import com.spitzer.designsystem.components.error.ErrorScreenView
import com.spitzer.designsystem.theme.RTheme

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RecipeDetailsScreenView(
    viewState: RecipeDetailsScreenViewState,
    modifier: Modifier = Modifier
) {
    val refreshing by remember { mutableStateOf(false) }
    val state = rememberPullRefreshState(refreshing, { viewState.onRefresh() })
    Box(
        contentAlignment = Alignment.TopStart,
        modifier = modifier
            .pullRefresh(state)
            .fillMaxSize()
            .background(RTheme.colors.n99n00)
    ) {
        if (viewState.isLoading) {
            LoadingView()
        } else if (viewState.recipeDetails != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                RecipeDetailsView(
                    viewState = viewState.recipeDetails
                )
            }
        } else {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Column {
                    LottieAnimationView(
                        animation = R.raw.search_not_found,
                        modifier = Modifier.height(250.dp)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
        PullRefreshIndicator(refreshing, state, Modifier.align(Alignment.TopCenter))

        IconButton(onClick = {
            viewState.onBackButtonPressed()
        }) {
            Image(
                painter = painterResource(id = R.drawable.arrow_back),
                contentDescription = stringResource(
                    id = R.string.back
                )
            )
        }

        AnimatedVisibility(
            visible = viewState.errorViewState != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            viewState.errorViewState?.let { viewState ->
                ErrorScreenView(
                    viewState = viewState
                )
            }
        }
    }
}
