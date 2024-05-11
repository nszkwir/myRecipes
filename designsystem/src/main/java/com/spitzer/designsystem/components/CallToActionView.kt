package com.spitzer.designsystem.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.spitzer.designsystem.theme.RTheme
import com.spitzer.designsystem.theme.Spacing

enum class CallToActionViewStyle {
    PRIMARY, WARNING
}

data class CallToActionViewState(
    val title: String,
    val isEnabled: Boolean = true,
    val style: CallToActionViewStyle = CallToActionViewStyle.PRIMARY,
    var onTap: () -> Unit
)

@Composable
fun CallToActionView(viewState: CallToActionViewState, modifier: Modifier = Modifier) {
    Button(
        onClick = viewState.onTap,
        enabled = viewState.isEnabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (viewState.style == CallToActionViewStyle.PRIMARY) RTheme.colors.p00p00 else RTheme.colors.r00r00,
            disabledContainerColor = RTheme.colors.n30n30
        ),
        shape = RoundedCornerShape(5.dp),
        contentPadding = PaddingValues(
            horizontal = Spacing.TWO.dp,
            vertical = Spacing.TWO.dp
        ),
        modifier = modifier
    ) {
        Text(
            text = viewState.title,
            textAlign = TextAlign.Center,
            color = RTheme.colors.n99n99,
            style = RTheme.typography.button,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Light Mode")
@Composable
fun PreviewCallToActionView() {
    RTheme {
        Surface(color = RTheme.colors.n99n00) {
            Column(
                modifier = Modifier.padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CallToActionView(
                    CallToActionViewState(
                        title = "PRIMARY",
                        style = CallToActionViewStyle.PRIMARY,
                        onTap = {}
                    )
                )
                CallToActionView(
                    CallToActionViewState(
                        title = "WARNING",
                        style = CallToActionViewStyle.WARNING,
                        onTap = {}
                    )
                )
            }
        }
    }
}
