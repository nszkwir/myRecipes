package com.spitzer.designsystem.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun RTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> RDarkColor
        else -> RLightColor
    }

    CompositionLocalProvider(
        LocalRColor provides colorScheme,
        LocalRTypography provides LocalRTypography.current,
        content = content
    )

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val context = (view.context as Activity)
            val window = context.window
            window.statusBarColor = colorScheme.n99n00.toArgb()
            window.navigationBarColor =
                colorScheme.n99n00.toArgb() // Set color of system navigationBar same as BottomNavigationView
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }
}

object RTheme {
    val colors: RColor
        @Composable
        get() = LocalRColor.current
    val typography: RTypography
        @Composable
        get() = LocalRTypography.current
}
