package com.spitzer.settings.settingsscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.spitzer.designsystem.theme.RTheme
import com.spitzer.designsystem.theme.Spacing

@Composable
fun SettingsScreenView(
    viewState: SettingsScreenViewState,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(RTheme.colors.n99n00)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = Spacing.TWO.dp)
        ) {
            Text(
                text = viewState.lightDarkModeSwitchViewState.text,
                color = RTheme.colors.n00n99,
                style = RTheme.typography.body1,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = viewState.lightDarkModeSwitchViewState.isChecked,
                onCheckedChange = viewState.lightDarkModeSwitchViewState.onCheckedChange
            )
        }
    }
}