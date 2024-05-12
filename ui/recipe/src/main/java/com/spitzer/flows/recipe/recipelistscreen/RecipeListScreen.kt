package com.spitzer.flows.recipe.recipelistscreen

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import com.spitzer.designsystem.theme.RTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeListScreen(
    output: (RecipeListScreenViewModelOutput) -> Unit
) {
    val viewModel: RecipeListScreenViewModel = hiltViewModel()
    viewModel.output = output

    val viewState: RecipeListScreenViewState by viewModel.viewState.collectAsState()
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = viewState.title,
                        color = RTheme.colors.n00n99,
                        style = RTheme.typography.heading2,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = RTheme.colors.n99n00
                )
            )
        }
    ) { innerPadding ->
        RecipeListScreenView(
            viewState = viewState,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

