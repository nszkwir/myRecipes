package com.spitzer.screenshot.configuration

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.RoborazziOptions
import com.github.takahirom.roborazzi.captureRoboImage
import com.google.accompanist.testharness.TestHarness
import kotlin.properties.ReadOnlyProperty

fun ComposeContentTestRule.setContentAndCapture(
    screenshotName: String,
    deviceName: String = "Pixel6",
    darkMode: Boolean = false,
    fontScale: FontScale = FontScale.ONE,
    roborazziOptions: RoborazziOptions = DefaultRoborazziOptions,
    content: @Composable () -> Unit
) {
    this.setContent {
        CompositionLocalProvider(
            LocalInspectionMode provides true,
        ) { TestHarness(fontScale = fontScale.value, darkMode = darkMode) { content() } }
    }

    this.onRoot().captureRoboImage(
        getScreenshotFilePath(screenshotName, deviceName, darkMode, fontScale.description),
        roborazziOptions = roborazziOptions
    )
}

fun AndroidComposeTestRule<*, *>.stringResource(@StringRes resId: Int) =
    ReadOnlyProperty<Any, String> { _, _ -> activity.getString(resId) }