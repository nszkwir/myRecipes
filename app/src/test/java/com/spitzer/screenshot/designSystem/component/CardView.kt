package com.spitzer.screenshot.designSystem.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.createComposeRule
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.spitzer.screenshot.configuration.FontScale
import com.spitzer.screenshot.configuration.setContentAndCapture
import com.spitzer.screenshot.designSystem.component.preview.CardViewPreview
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(
    sdk = [28],
    qualifiers = RobolectricDeviceQualifiers.Pixel6
)
class CardViewScreenshotTest {

    @get:Rule
    val composeRule = createComposeRule()
    private val screenshotName = "DesignSystem/CardView/CardView"

    @Test
    fun fontScaleONE_LightMode() {
        composeRule.setContentAndCapture(
            screenshotName = screenshotName, fontScale = FontScale.ONE, darkMode = false
        ) { ComposeView() }
    }

    @Test
    @Config(qualifiers = "+night")
    fun fontScaleONE_DarkMode() {
        composeRule.setContentAndCapture(
            screenshotName = screenshotName, fontScale = FontScale.ONE, darkMode = true
        ) { ComposeView() }
    }

    @Test
    fun fontScaleTWO_LightMode() {
        composeRule.setContentAndCapture(
            screenshotName = screenshotName, fontScale = FontScale.TWO, darkMode = false
        ) { ComposeView() }
    }

    @Test
    @Config(qualifiers = "+night")
    fun fontScaleTWO_DarkMode() {
        composeRule.setContentAndCapture(
            screenshotName = screenshotName, fontScale = FontScale.TWO, darkMode = true
        ) { ComposeView() }
    }

    @Composable
    private fun ComposeView() {
        CardViewPreview()
    }
}
