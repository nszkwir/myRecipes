package com.spitzer.designsystem.components.error

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import com.spitzer.designsystem.R
import com.spitzer.designsystem.animations.LottieAnimationView
import com.spitzer.designsystem.components.CallToActionView
import com.spitzer.designsystem.components.CallToActionViewState
import com.spitzer.designsystem.components.CallToActionViewStyle
import com.spitzer.designsystem.theme.RTheme
import com.spitzer.designsystem.theme.Spacing

@Composable
fun ErrorScreenView(
    viewState: ErrorViewState,
    modifier: Modifier = Modifier
) {
    var buttonMinWidth by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(RTheme.colors.n99n00)
            .testTag(stringResource(id = R.string.errorView_TestTag))
    ) {
        Text(
            text = viewState.subtitle,
            textAlign = TextAlign.Center,
            color = RTheme.colors.n00n99,
            style = RTheme.typography.body1,
            modifier = Modifier
                .padding(horizontal = Spacing.TWO.dp)
                .padding(bottom = Spacing.TWO.dp)
        )
        LottieAnimationView(
            animation = R.raw.error,
            modifier = Modifier
                .height(250.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.TWO.dp),
            modifier = Modifier.padding(Spacing.TWO.dp)
        ) {
            viewState.secondaryButtonViewState?.let { viewState ->
                CallToActionView(
                    viewState = viewState,
                    modifier = Modifier
                        .onGloballyPositioned {
                            val width = with(density) {
                                it.size.width.toDp()
                            }
                            buttonMinWidth = max(width, buttonMinWidth)
                        }
                        .defaultMinSize(minWidth = buttonMinWidth)
                        .testTag(stringResource(id = R.string.errorView_CancelButton_TestTag))
                )
            }
            CallToActionView(
                viewState = viewState.primaryButtonViewState,
                modifier = Modifier
                    .onGloballyPositioned {
                        val width = with(density) {
                            it.size.width.toDp()
                        }
                        buttonMinWidth = max(width, buttonMinWidth)
                    }
                    .defaultMinSize(minWidth = buttonMinWidth)
                    .testTag(stringResource(id = R.string.errorView_RetryButton_TestTag))
            )
        }
    }
}

@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Light Mode")
@Composable
fun PreviewRPErrorScreenView() {
    RTheme {
        Surface(color = RTheme.colors.n99n00) {
            ErrorScreenView(
                viewState = ErrorViewState(
                    subtitle = "Error subtitle",
                    primaryButtonViewState = CallToActionViewState(
                        title = "Retry",
                        onTap = { }
                    ),
                    secondaryButtonViewState = CallToActionViewState(
                        title = "Cancel",
                        style = CallToActionViewStyle.WARNING,
                        onTap = {}
                    )
                )
            )
        }
    }
}