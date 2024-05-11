package com.spitzer.designsystem.utils

import android.util.Size
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

@Composable
fun getDeviceResolution(): Size {
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val width = with(density) { configuration.screenWidthDp.dp.roundToPx() }
    val height = with(density) { configuration.screenHeightDp.dp.roundToPx() }
    return Size(width, height)
}
