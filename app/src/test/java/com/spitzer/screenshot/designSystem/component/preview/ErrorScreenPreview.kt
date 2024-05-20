package com.spitzer.screenshot.designSystem.component.preview

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import com.spitzer.designsystem.components.CallToActionViewState
import com.spitzer.designsystem.components.CallToActionViewStyle
import com.spitzer.designsystem.components.error.ErrorScreenView
import com.spitzer.designsystem.components.error.ErrorViewState
import com.spitzer.designsystem.theme.RTheme

@Composable
fun ErrorScreenViewPreview() {
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
