package com.spitzer.screenshot.designSystem.component.preview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.spitzer.designsystem.components.CallToActionView
import com.spitzer.designsystem.components.CallToActionViewState
import com.spitzer.designsystem.components.CallToActionViewStyle
import com.spitzer.designsystem.theme.RTheme

@Composable
fun CallToActionViewPreview() {
    RTheme {
        Surface(color = RTheme.colors.n99n00) {
            Column(
                modifier = Modifier.padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CallToActionView(
                    CallToActionViewState(
                        title = "PRIMARY",
                        style = CallToActionViewStyle.PRIMARY,
                        onTap = {}
                    )
                )
                CallToActionView(
                    CallToActionViewState(
                        title = "WARNING",
                        style = CallToActionViewStyle.WARNING,
                        onTap = {}
                    )
                )
            }
        }
    }
}