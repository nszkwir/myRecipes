package com.spitzer.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.spitzer.designsystem.R

private val roboto = FontFamily(
    Font(R.font.roboto_regular, FontWeight.Normal),
    Font(R.font.roboto_bold, FontWeight.Bold),
    Font(R.font.roboto_black, FontWeight.Black),
    Font(R.font.roboto_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.roboto_bold_italic, FontWeight.Bold, FontStyle.Italic),
    Font(R.font.roboto_black_italic, FontWeight.Black, FontStyle.Italic),
)

@Immutable
data class RTypography(
    val heading1: TextStyle,
    val heading2: TextStyle,
    val heading3: TextStyle,
    val body1: TextStyle,
    val body2: TextStyle,
    val button: TextStyle,
    val caption1: TextStyle,
    val caption2: TextStyle
)

val LocalRTypography = staticCompositionLocalOf {
    RTypography(
        heading1 = TextStyle(
            fontFamily = roboto,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            lineHeight = 34.sp,
            letterSpacing = 0.12.sp
        ),
        heading2 = TextStyle(
            fontFamily = roboto,
            fontWeight = FontWeight.Normal,
            fontSize = 24.sp,
            lineHeight = 28.sp,
            letterSpacing = 0.12.sp
        ),
        heading3 = TextStyle(
            fontFamily = roboto,
            fontWeight = FontWeight.Normal,
            fontSize = 19.sp,
            lineHeight = 23.sp,
            letterSpacing = 0.15.sp
        ),
        body1 = TextStyle(
            fontFamily = roboto,
            fontWeight = FontWeight.Normal,
            fontSize = 17.sp,
            lineHeight = 22.sp,
            letterSpacing = 0.5.sp
        ),
        body2 = TextStyle(
            fontFamily = roboto,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.5.sp
        ),
        button = TextStyle(
            fontFamily = roboto,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 18.sp,
            letterSpacing = 0.25.sp
        ),
        caption1 = TextStyle(
            fontFamily = roboto,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 18.sp,
            letterSpacing = 0.25.sp
        ),
        caption2 = TextStyle(
            fontFamily = roboto,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            lineHeight = 13.sp,
            letterSpacing = 0.4.sp
        )
    )
}
