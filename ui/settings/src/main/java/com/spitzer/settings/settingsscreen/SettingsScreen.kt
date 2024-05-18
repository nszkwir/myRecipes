package com.spitzer.settings.settingsscreen

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import com.spitzer.designsystem.theme.RTheme
import com.spitzer.designsystem.theme.Spacing

@Composable
fun SettingsScreen(output: (SettingsScreenViewModelOutput) -> Unit) {
    val viewModel: SettingsScreenViewModel = hiltViewModel()
    viewModel.output = output

    val viewState: SettingsScreenViewState by viewModel.viewState.collectAsState()
    Scaffold(
        topBar = {
            Text(
                text = viewState.title,
                textAlign = TextAlign.Center,
                color = RTheme.colors.n00n99,
                style = RTheme.typography.heading2,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.TWO.dp)
            )
        },
        containerColor = RTheme.colors.n99n00
    ) { innerPadding ->
        SettingsScreenView(
            viewState = viewState,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
