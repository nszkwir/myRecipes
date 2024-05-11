package com.spitzer.designsystem.animations

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NavBackStackEntry

data class PushTransition(val deviceWidthResolution: Int) {

    companion object {
        const val durationMillis = 300L
    }

    private val slideEffect = tween<IntOffset>(durationMillis = durationMillis.toInt())

    val enter: (AnimatedContentTransitionScope<NavBackStackEntry>) -> EnterTransition = {
        slideInHorizontally(
            initialOffsetX = { deviceWidthResolution },
            animationSpec = slideEffect
        )
    }

    val exit: (AnimatedContentTransitionScope<NavBackStackEntry>) -> ExitTransition = {
        slideOutHorizontally(
            targetOffsetX = { -deviceWidthResolution },
            animationSpec = slideEffect
        )
    }

    val popEnter: (AnimatedContentTransitionScope<NavBackStackEntry>) -> EnterTransition = {
        slideInHorizontally(
            initialOffsetX = { -deviceWidthResolution },
            animationSpec = slideEffect
        )
    }

    val popExit: (AnimatedContentTransitionScope<NavBackStackEntry>) -> ExitTransition = {
        slideOutHorizontally(
            targetOffsetX = { deviceWidthResolution },
            animationSpec = slideEffect
        )
    }
}