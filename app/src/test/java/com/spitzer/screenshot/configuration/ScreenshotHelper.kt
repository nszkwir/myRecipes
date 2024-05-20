package com.spitzer.screenshot.configuration

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onRoot
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.RoborazziOptions
import com.github.takahirom.roborazzi.captureRoboImage
import com.google.accompanist.testharness.TestHarness
import com.spitzer.designsystem.theme.RTheme
import org.robolectric.RuntimeEnvironment

@OptIn(ExperimentalRoborazziApi::class)
val DefaultRoborazziOptions =
    RoborazziOptions(
        // Pixel-perfect matching
        compareOptions = RoborazziOptions.CompareOptions(changeThreshold = 0f),
        // Reduce the size of the PNGs
        recordOptions = RoborazziOptions.RecordOptions(resizeScale = 0.5),
    )

const val screenshotsHomePath = "src/test/screenshots/"

fun getScreenshotFilePath(
    screenshotName: String,
    deviceName: String,
    darkMode: Boolean = false,
    fontScale: String = "",
): String {
    val mode = if (darkMode) "_Dark" else "_Light"

    return "$screenshotsHomePath${screenshotName}_$deviceName${fontScale}${mode}.png"
}

enum class DefaultTestDevices(val description: String, val spec: String) {
    SMALLPHONE("SmallPhone", "spec:shape=Normal,width=360,height=640,unit=dp,dpi=480"),
    PHONE("Pixel6", "spec:shape=Normal,width=411,height=914,unit=dp,dpi=480"),
    LANDSCAPE("Landscape", "spec:shape=Normal,width=914,height=411,unit=dp,dpi=480"),
    // Enable more device configurations if needed
    //FOLDABLE("Foldable", "spec:shape=Normal,width=673,height=841,unit=dp,dpi=480"),
    //TABLET("Tablet", "spec:shape=Normal,width=1280,height=800,unit=dp,dpi=480"),
}

fun <A : ComponentActivity> AndroidComposeTestRule<ActivityScenarioRule<A>, A>.captureMultiDeviceMultiMode(
    screenshotName: String,
    body: @Composable () -> Unit,
) {
    DefaultTestDevices.entries.forEach {
        this.captureForDevice(
            it.description,
            it.spec,
            screenshotName,
            darkMode = true,
            fontScale = FontScale.ONE,
            body = body
        )
        this.captureForDevice(
            it.description,
            it.spec,
            screenshotName,
            darkMode = false,
            fontScale = FontScale.ONE,
            body = body
        )
        this.captureForDevice(
            it.description,
            it.spec,
            screenshotName,
            darkMode = true,
            fontScale = FontScale.TWO,
            body = body
        )
        this.captureForDevice(
            it.description,
            it.spec,
            screenshotName,
            darkMode = false,
            fontScale = FontScale.TWO,
            body = body
        )
    }
}

fun <A : ComponentActivity> AndroidComposeTestRule<ActivityScenarioRule<A>, A>.captureMultiDevice(
    screenshotName: String,
    darkMode: Boolean = false,
    fontScale: FontScale = FontScale.ONE,
    body: @Composable () -> Unit,
) {
    DefaultTestDevices.entries.forEach {
        this.captureForDevice(
            it.description,
            it.spec,
            screenshotName,
            darkMode = darkMode,
            fontScale = fontScale,
            body = body
        )
    }
}

enum class FontScale(val description: String, val value: Float) {
    ONE("", 1f),
    TWO("_2x", 2f),
}

fun <A : ComponentActivity> AndroidComposeTestRule<ActivityScenarioRule<A>, A>.captureForDevice(
    deviceName: String,
    deviceSpec: String,
    screenshotName: String,
    roborazziOptions: RoborazziOptions = DefaultRoborazziOptions,
    darkMode: Boolean = false,
    fontScale: FontScale = FontScale.ONE,
    body: @Composable () -> Unit,
) {
    val (width, height, dpi) = extractSpecs(deviceSpec)
    // Set qualifiers from specs
    RuntimeEnvironment.setQualifiers("w${width}dp-h${height}dp-${dpi}dpi")

    this.activity.setContent {
        CompositionLocalProvider(
            LocalInspectionMode provides true,
        ) {
            TestHarness(darkMode = darkMode, fontScale = fontScale.value) {
                RTheme(
                    darkTheme = darkMode
                ) {
                    body()
                }
            }
        }
    }

    this.onRoot()
        .captureRoboImage(
            getScreenshotFilePath(screenshotName, deviceName, darkMode, fontScale.description),
            roborazziOptions = roborazziOptions,
        )
}

fun <A : ComponentActivity> AndroidComposeTestRule<ActivityScenarioRule<A>, A>.captureMultiTheme(
    name: String,
    overrideFileName: String? = null,
    shouldCompareDarkMode: Boolean = true,
    content: @Composable (desc: String) -> Unit,
) {
    val darkModeValues = if (shouldCompareDarkMode) listOf(true, false) else listOf(false)

    var darkMode by mutableStateOf(true)

    this.setContent {
        CompositionLocalProvider(
            LocalInspectionMode provides true,
        ) {
            RTheme(
                darkTheme = darkMode
            ) {
                // Keying is necessary in some cases (e.g. animations)
                key(darkMode) {
                    val description = generateDescription(
                        shouldCompareDarkMode,
                        darkMode
                    )
                    content(description)
                }
            }
        }
    }

    // Create permutations
    darkModeValues.forEach { isDarkMode ->
        darkMode = isDarkMode
        val darkModeDesc = if (isDarkMode) "dark" else "light"

        val filename = overrideFileName ?: name

        this.onRoot()
            .captureRoboImage(
                screenshotsHomePath +
                        "$name/$filename" +
                        "_$darkModeDesc" +
                        ".png",
                roborazziOptions = DefaultRoborazziOptions,
            )
    }
}

@Composable
private fun generateDescription(
    shouldCompareDarkMode: Boolean,
    darkMode: Boolean
): String {
    val description = "" +
            if (shouldCompareDarkMode) {
                if (darkMode) "Dark" else "Light"
            } else {
                ""
            }
    return description.trim()
}

/**
 * Extracts some properties from the spec string. Note that this function is not exhaustive.
 */
private fun extractSpecs(deviceSpec: String): TestDeviceSpecs {
    val specs = deviceSpec.substringAfter("spec:")
        .split(",").map { it.split("=") }.associate { it[0] to it[1] }
    val width = specs["width"]?.toInt() ?: 640
    val height = specs["height"]?.toInt() ?: 480
    val dpi = specs["dpi"]?.toInt() ?: 480
    return TestDeviceSpecs(width, height, dpi)
}

data class TestDeviceSpecs(val width: Int, val height: Int, val dpi: Int)
