package com.spitzer.designsystem.theme

import android.content.Context
import android.content.res.Configuration
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

data class ThemeManager @Inject constructor(@ApplicationContext val context: Context) {

    fun isDarkModeEnabled(): Boolean {
        val currentNightMode =
            context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES
    }
}
