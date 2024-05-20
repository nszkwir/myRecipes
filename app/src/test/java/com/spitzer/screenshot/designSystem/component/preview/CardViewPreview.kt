package com.spitzer.screenshot.designSystem.component.preview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.spitzer.designsystem.components.CardView
import com.spitzer.designsystem.components.CardViewState
import com.spitzer.designsystem.theme.RTheme
import com.spitzer.designsystem.theme.Spacing

@Composable
fun CardViewPreview() {
    RTheme {
        Surface(color = RTheme.colors.n99n00) {
            Column(
                verticalArrangement = Arrangement.spacedBy(Spacing.TWO.dp),
                modifier = Modifier
                    .padding(Spacing.TWO.dp)
                    .fillMaxSize()
            ) {
                CardView(
                    viewState = CardViewState(
                        firstTitle = "Pasta On The Border",
                        secondTitle = "Need a <b>diary free main course</b>? Pastan On The Border could be an outstanding recipe to try."
                    )
                )
            }
        }
    }
}
