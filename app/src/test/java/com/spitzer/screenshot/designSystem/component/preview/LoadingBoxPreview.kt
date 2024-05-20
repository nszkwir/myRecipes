package com.spitzer.screenshot.designSystem.component.preview

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import com.spitzer.designsystem.components.LoadingBox
import com.spitzer.designsystem.theme.RTheme

@Composable
fun LoadingBoxPreview() {
    RTheme {
        Surface(color = RTheme.colors.n99n00) {
            LoadingBox()
        }
    }
}