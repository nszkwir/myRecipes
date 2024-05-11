package com.spitzer.designsystem.animations

import androidx.annotation.RawRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition

@Composable
fun LottieAnimationView(
    @RawRes animation: Int,
    modifier: Modifier = Modifier,
    shouldLoop: Boolean = false,
    onAnimationFinishedPlaying: (() -> Unit)? = null
) {
    val isPlaying by remember { mutableStateOf(true) }
    val speed by remember { mutableFloatStateOf(1f) }

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(animation))

    // to control the animation
    val progress by animateLottieCompositionAsState(
        // pass the composition created above
        composition = composition,

        // Iterates Forever
        iterations = if (shouldLoop) LottieConstants.IterateForever else 1,

        // pass isPlaying we created above,
        // changing isPlaying will recompose
        // Lottie and pause/play
        isPlaying = isPlaying,

        // pass speed we created above,
        // changing speed will increase Lottie
        speed = speed,

        // this makes animation to restart
        // when paused and play
        // pass false to continue the animation
        // at which it was paused
        restartOnPlay = false
    )

    LottieAnimation(
        composition = composition,
        modifier = modifier,
        progress = { progress }
    )

    if (!shouldLoop && progress == 1.0f) {
        onAnimationFinishedPlaying?.invoke()
    }
}
