package com.spitzer.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.spitzer.designsystem.R
import com.spitzer.designsystem.animations.LottieAnimationView
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun LoadingView(
    modifier: Modifier = Modifier,
    shouldDelay: Boolean = true
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (shouldDelay) {
            delay(500.milliseconds)
        }
        visible = true
    }
    if (visible) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
                .fillMaxSize()
                .background(color = Color.Transparent)
        ) {
            LottieAnimationView(
                animation = R.raw.loading,
                shouldLoop = true,
                modifier = Modifier.size(width = 100.dp, height = 100.dp)
            )
        }
    }
}

