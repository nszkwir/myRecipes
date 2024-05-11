package com.spitzer.designsystem.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.spitzer.designsystem.R
import com.spitzer.designsystem.animations.LottieAnimationView
import com.spitzer.designsystem.extensions.advancedShadow
import com.spitzer.designsystem.extensions.shimmerEffect
import com.spitzer.designsystem.theme.RTheme
import com.spitzer.designsystem.theme.Spacing
import java.net.URL

data class CardViewState(
    val topImageURL: URL? = null,
    val iconButtonURL: URL? = null,
    val firstTitle: String? = null,
    val secondTitle: String? = null,
    val isLoading: Boolean = false,
    val onTap: Action? = null,
    val onIconButtonTapped: Action? = null
)

@Composable
fun CardView(viewState: CardViewState, modifier: Modifier = Modifier) {
    if (viewState.isLoading) {
        LoadingView(modifier = modifier)
    } else {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .advancedShadow(
                    color = RTheme.colors.n30n30,
                    alpha = 0.5f,
                    cornersRadius = 10.dp,
                    shadowBlurRadius = 4.dp,
                    offsetY = 2.dp
                )
                .clip(RoundedCornerShape(10.dp))
                .background(RTheme.colors.n00n99)
                .clickable { viewState.onTap?.action?.invoke() }
        ) {
            TopSectionView(
                viewState = viewState,
                modifier = Modifier
                    .background(RTheme.colors.p00p00)
                    .aspectRatio(104f / 77f)
            )
            BottomSectionView(
                viewState = viewState
            )
        }
    }
}

@Composable
private fun LoadingView(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(RTheme.colors.p00p00)
            .shimmerEffect()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(104f / 77f)
                .background(Color.Transparent)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(Color.Transparent)
        )
    }
}

@Composable
private fun TopSectionView(viewState: CardViewState, modifier: Modifier) {
    viewState.topImageURL?.let {
        AsyncImage(
            model = it.toString(),
            contentDescription = "",
            modifier = modifier
        )
    } ?: run {
        LottieAnimationView(
            animation = R.raw.empty_search,
            modifier = modifier
        )
    }
}

@Composable
private fun BottomSectionView(viewState: CardViewState, modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.spacedBy(Spacing.ONE.dp),
        modifier = modifier
            .background(RTheme.colors.n80n20)
            .padding(Spacing.TWO.dp)
    ) {
        viewState.firstTitle?.let { text ->
            Text(
                text = text,
                color = RTheme.colors.n20n80,
                style = RTheme.typography.body1,
                fontWeight = FontWeight.Bold
            )
        }
        viewState.secondTitle?.let { text ->
            HTMLTextView(
                text = text,
                color = RTheme.colors.n20n80,
                style = RTheme.typography.body2,
                maxLines = 3
            )
        }
    }
}

@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Light Mode")
@Composable
fun PreviewRPCardView() {
    RTheme {
        Surface(color = RTheme.colors.n99n00) {
            Column(
                verticalArrangement = Arrangement.spacedBy(Spacing.TWO.dp),
                modifier = Modifier
                    .padding(Spacing.TWO.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                CardView(viewState = CardViewState(isLoading = true))
                CardView(
                    viewState = CardViewState(
                        topImageURL = URL("https://img.spoonacular.com/recipes/654959-312x231.jpg"),
                        firstTitle = "Pasta On The Border",
                        secondTitle = "Need a <b>diary free main course</b>? Pastan On The Border could be an outstanding recipe to try."
                    )
                )
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