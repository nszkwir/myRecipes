package com.spitzer.designsystem.extensions

import android.graphics.Typeface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontSynthesis
import androidx.compose.ui.text.font.FontWeight

@Composable
fun TextStyle.toTypeface(fontWeight: FontWeight? = null): Typeface {
    val resolver: FontFamily.Resolver = LocalFontFamilyResolver.current
    val typeface: Typeface = remember(resolver, this) {
        resolver.resolve(
            fontFamily = this.fontFamily,
            fontWeight = fontWeight ?: FontWeight.Normal,
            fontStyle = this.fontStyle ?: FontStyle.Normal,
            fontSynthesis = this.fontSynthesis ?: FontSynthesis.All,
        )
    }.value as Typeface
    return typeface
}
