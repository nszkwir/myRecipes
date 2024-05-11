package com.spitzer.designsystem.components

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.spitzer.designsystem.theme.RTheme
import com.spitzer.designsystem.theme.Spacing

data class PillViewState(
    val text: String,
    val isSelected: Boolean,
    val onSelectionChange: (Boolean) -> Unit
)

@Composable
fun PillView(
    viewState: PillViewState,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = {
            viewState.onSelectionChange(!viewState.isSelected)
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = if (viewState.isSelected) RTheme.colors.p00p00 else RTheme.colors.n30n30
        ),
        border = BorderStroke(0.dp, Color.Transparent),
        shape = RoundedCornerShape(40.dp),
        contentPadding = PaddingValues(
            horizontal = Spacing.TWO.dp,
            vertical = 12.dp
        ),
        modifier = modifier
            .defaultMinSize(minWidth = 80.dp, minHeight = 40.dp)
    ) {
        Text(
            text = viewState.text,
            textAlign = TextAlign.Center,
            color = RTheme.colors.n99n99,
            style = RTheme.typography.body1,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Light Mode")
@Composable
fun PreviewPillView() {
    RTheme {
        Surface(color = RTheme.colors.n99n00) {
            Column(
                modifier = Modifier.padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PillView(PillViewState("Filter option", true) {})
                PillView(PillViewState("Sorting option", false) {})
            }
        }
    }
}