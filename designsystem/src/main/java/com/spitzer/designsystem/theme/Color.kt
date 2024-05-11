package com.spitzer.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class RColor(
    val n00n00: Color,
    val n00n99: Color,
    val n20n80: Color,
    val n30n30: Color,
    val n80n20: Color,
    val n99n00: Color,
    val n99n99: Color,
    val p00p00: Color,
    val r00r00: Color
)

val RLightColor = RColor(
    n00n00 = Color(red = 0, green = 0, blue = 0),
    n00n99 = Color(red = 0, green = 0, blue = 0),
    n20n80 = Color(red = 17, green = 20, blue = 28),
    n30n30 = Color(red = 130, green = 130, blue = 130),
    n80n20 = Color(red = 238, green = 235, blue = 227),
    n99n00 = Color(red = 255, green = 255, blue = 255),
    n99n99 = Color(red = 255, green = 255, blue = 255),
    p00p00 = Color(red = 95, green = 154, blue = 92),
    r00r00 = Color(red = 255, green = 54, blue = 92)
)

val RDarkColor = RColor(
    n00n00 = Color(red = 0, green = 0, blue = 0),
    n00n99 = Color(red = 255, green = 255, blue = 255),
    n20n80 = Color(red = 238, green = 235, blue = 227),
    n30n30 = Color(red = 130, green = 130, blue = 130),
    n80n20 = Color(red = 17, green = 20, blue = 28),
    n99n00 = Color(red = 0, green = 0, blue = 0),
    n99n99 = Color(red = 255, green = 255, blue = 255),
    p00p00 = Color(red = 95, green = 154, blue = 92),
    r00r00 = Color(red = 255, green = 54, blue = 92)
)

val LocalRColor = staticCompositionLocalOf {
    RLightColor
}
