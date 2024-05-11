package com.spitzer.designsystem.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class Spacing(private val value: Int) {
    HALF(4),
    ONE(8),
    TWO(16);

    val dp: Dp
        get() = value.dp
}
