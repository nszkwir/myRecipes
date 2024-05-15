package com.spitzer.flows.recipe.recipedetailsscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.spitzer.designsystem.animations.LottieAnimationView
import com.spitzer.designsystem.components.Action
import com.spitzer.designsystem.components.HTMLTextView
import com.spitzer.designsystem.components.LinkHTMLTextView
import com.spitzer.designsystem.components.LoadingView
import com.spitzer.designsystem.extensions.advancedShadow
import com.spitzer.designsystem.theme.RTheme
import com.spitzer.designsystem.theme.Spacing
import com.spitzer.recipe.R
import java.net.URL
import com.spitzer.designsystem.R as dsR

data class RecipeDetailsViewState(
    val imageUrl: URL? = null,
    val title: String? = null,
    val summary: String? = null,
    val instructions: String? = null,
    val readyInMinutes: Int? = null,
    val servings: Int? = null,
    val calories: Int? = null,
    val vegan: Boolean? = null,
    val spoonacularScore: Double? = null,
    val spoonacularSourceUrl: URL? = null,
    val isFavorite: Boolean = false,
    val isLoading: Boolean = false,
    val onFavoriteTapped: Action? = null,
    val ingredients: List<String>? = null
)

@Composable
fun RecipeDetailsView(
    modifier: Modifier = Modifier,
    viewState: RecipeDetailsViewState,
) {
    if (viewState.isLoading) {
        LoadingView(modifier = modifier)
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
        ) {
            TopSectionView(
                modifier = Modifier
                    .background(RTheme.colors.p00p00)
                    .aspectRatio(278f / 185f),
                viewState = viewState
            )
            BottomSectionView(
                modifier = Modifier,
                viewState = viewState
            )
        }
    }
}

@Composable
private fun TopSectionView(
    modifier: Modifier = Modifier,
    viewState: RecipeDetailsViewState
) {
    Box(modifier = modifier) {
        viewState.imageUrl?.let {
            AsyncImage(
                model = it.toString(),
                contentDescription = null,
                modifier = modifier
            )
        } ?: run {
            LottieAnimationView(
                animation = dsR.raw.empty_search,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun BottomSectionView(
    modifier: Modifier = Modifier,
    viewState: RecipeDetailsViewState
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(Spacing.TWO.dp),
        modifier = modifier
            .fillMaxSize()
            .background(RTheme.colors.n80n20)
            .padding(Spacing.TWO.dp)
    ) {
        RecipeTitleView(viewState = viewState)
        RecipeInfoView(viewState = viewState)
        RecipeSummaryView(viewState = viewState)
        RecipeIngredientsView(viewState = viewState)
        RecipeInstructionsView(viewState = viewState)
        RecipeSourceLinkView(viewState = viewState)
    }
}

@Composable
private fun RecipeTitleView(
    modifier: Modifier = Modifier,
    viewState: RecipeDetailsViewState
) {
    var showSplashAnimation by remember { mutableStateOf(false) }

    val (heartPainter, contentDescription) = if (viewState.isFavorite) {
        Pair(
            painterResource(id = dsR.drawable.heart_filled),
            stringResource(id = R.string.recipe_details_screen_favorite)
        )
    } else {
        Pair(
            painterResource(id = dsR.drawable.heart_unfilled),
            stringResource(id = R.string.recipe_details_screen_no_favorite)
        )
    }
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(Spacing.TWO.dp)
    ) {
        viewState.title?.let { text ->
            Text(
                modifier = Modifier.weight(1f),
                text = text,
                color = RTheme.colors.n20n80,
                style = RTheme.typography.heading2,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Bold
            )
        }
        Box(contentAlignment = Alignment.Center) {
            Image(
                painter = heartPainter,
                contentDescription = "",
                modifier = Modifier
                    .clickable(indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = {
                            showSplashAnimation = !viewState.isFavorite
                            viewState.onFavoriteTapped?.action?.invoke()
                        })
                    .padding(horizontal = Spacing.ONE.dp)
                    .size(40.dp)
                    .advancedShadow()
                    .semantics {
                        this.contentDescription = contentDescription
                    }
            )
            if (showSplashAnimation) {
                LottieAnimationView(
                    animation = com.spitzer.designsystem.R.raw.explosion,
                    onAnimationFinishedPlaying = {
                        showSplashAnimation = false
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .scale(3f)
                )
            }
        }
    }
}

@Composable
private fun RecipeInfoView(
    modifier: Modifier = Modifier,
    viewState: RecipeDetailsViewState
) {
    val isSectionVisible = !(viewState.readyInMinutes == null &&
            viewState.spoonacularScore == null && viewState.servings == null &&
            viewState.vegan == null)
    if (isSectionVisible) {
        Column(
            verticalArrangement = Arrangement.spacedBy(Spacing.ONE.dp),
            horizontalAlignment = Alignment.Start,
            modifier = modifier
                .fillMaxWidth()
        ) {
            viewState.readyInMinutes?.let {
                RecipeDetailsRow(
                    text = stringResource(
                        id = R.string.recipe_details_screen_ready_in_minutes,
                        it.toString()
                    ),
                    painter = painterResource(id = dsR.drawable.on_time)
                )
            }
            viewState.spoonacularScore?.let {
                RecipeDetailsRow(
                    text = stringResource(
                        id = R.string.recipe_details_screen_score,
                        String.format("%.1f", it)
                    ),
                    painter = painterResource(id = dsR.drawable.score)
                )
            }
            viewState.servings?.let {
                RecipeDetailsRow(
                    text = stringResource(
                        id = R.string.recipe_details_screen_servings,
                        it.toString()
                    ),
                    painter = painterResource(id = dsR.drawable.servings)
                )
            }
            if (viewState.vegan == true) {
                RecipeDetailsRow(
                    text = stringResource(id = R.string.recipe_details_screen_vegan),
                    painter = painterResource(id = dsR.drawable.vegan)
                )
            }
        }
    }
}

@Composable
private fun RecipeIngredientsView(
    modifier: Modifier = Modifier,
    viewState: RecipeDetailsViewState
) {
    if (!viewState.ingredients.isNullOrEmpty()) {
        Column(
            verticalArrangement = Arrangement.spacedBy(Spacing.ONE.dp),
            horizontalAlignment = Alignment.Start,
            modifier = modifier
                .fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = R.string.recipe_details_screen_ingredients),
                color = RTheme.colors.n20n80,
                style = RTheme.typography.body2,
                fontWeight = FontWeight.Bold
            )
            viewState.ingredients.forEach {
                IngredientDetailsRow(ingredient = it)
            }
        }
    }
}

@Composable
private fun IngredientDetailsRow(
    modifier: Modifier = Modifier,
    ingredient: String
) {
    Box(modifier = modifier.padding(vertical = 1.dp)) {
        Box(modifier = Modifier.padding(vertical = 2.dp)) {
            Image(
                modifier = Modifier
                    .size(12.dp)
                    .padding(top = 4.dp),
                painter = painterResource(id = dsR.drawable.circle),
                colorFilter = ColorFilter.tint(color = RTheme.colors.n20n80),
                contentDescription = null
            )
        }
        Text(
            modifier = Modifier.padding(start = 24.dp),
            text = ingredient,
            maxLines = 4,
            color = RTheme.colors.n20n80,
            style = RTheme.typography.body2,
            fontWeight = FontWeight.Normal
        )
    }
}

@Composable
private fun RecipeDetailsRow(
    modifier: Modifier = Modifier,
    text: String,
    painter: Painter
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(Spacing.ONE.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Image(
            modifier = Modifier.size(20.dp),
            painter = painter,
            colorFilter = ColorFilter.tint(color = RTheme.colors.n20n80),
            contentDescription = null
        )
        Text(
            text = text,
            color = RTheme.colors.n20n80,
            style = RTheme.typography.caption2,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun RecipeSummaryView(
    modifier: Modifier = Modifier,
    viewState: RecipeDetailsViewState
) {
    viewState.summary?.let { text ->
        Row(
            modifier = modifier
        ) {
            HTMLTextView(
                text = text,
                color = RTheme.colors.n20n80,
                style = RTheme.typography.body2,
            )
        }
    }
}

@Composable
private fun RecipeInstructionsView(
    viewState: RecipeDetailsViewState
) {
    viewState.instructions?.let { text ->
        Text(
            text = stringResource(id = R.string.recipe_details_screen_instructions),
            color = RTheme.colors.n20n80,
            style = RTheme.typography.body2,
            fontWeight = FontWeight.Bold
        )
        HTMLTextView(
            text = text,
            color = RTheme.colors.n20n80,
            style = RTheme.typography.body2,
        )
    }
}

@Composable
private fun RecipeSourceLinkView(
    viewState: RecipeDetailsViewState
) {
    viewState.spoonacularSourceUrl?.let {
        LinkHTMLTextView(urlString = viewState.spoonacularSourceUrl.toString())
    }
}

@Composable
@Preview(showBackground = true)
fun BottomSectionView_Preview() {
    Column(modifier = Modifier) {
        BottomSectionView(viewState = demoViewState.copy(isFavorite = true))
    }
}

private val demoViewState = RecipeDetailsViewState(
    imageUrl = null,
    title = "Italian Pasta Salad with organic Arugula",
    summary = "The recipe Italian Pasta Salad with organic Arugula could satisfy your Mediterranean craving in approximately <b>45 minutes</b>. One portion of this dish contains approximately <b>28g of protein</b>, <b>20g of fat</b>, and a total of <b>696 calories</b>. For <b>\$3.84 per serving</b>, you get a main course that serves 4. 1 person has tried and liked this recipe. It is brought to you by Foodista. A mixture of pecorino romano cheese, oregano, botticelli extra virgin olive oil, and a handful of other ingredients are all it takes to make this recipe so flavorful. Taking all factors into account, this recipe <b>earns a spoonacular score of 85%</b>, which is amazing. If you like this recipe, take a look at these similar recipes: <a href=\\\"https://spoonacular.com/recipes/weight-watchers-italian-arugula-salad-525364\\\">Weight Watchers Italian Arugula Salad</a>, <a href=\\\"https://spoonacular.com/recipes/arugula-italian-tuna-and-white-bean-salad-7940\\\">Arugula, Italian Tuna, and White Bean Salad</a>, and <a href=\\\"https://spoonacular.com/recipes/arugula-pasta-salad-547702\\\">Arugula Pasta Salad</a>.",
    instructions = "<ol><li>Boil Pasta</li><li>Meanwhile in a pasta bowl add arugala, sundried tomatoes, lemon, chick peas, olive oil, cheese add pasta top with herbs and salt and pepper</li></ol>",
    readyInMinutes = 15,
    servings = 4,
    calories = 200,
    vegan = true,
    spoonacularScore = 4.2,
    spoonacularSourceUrl = URL("https://spoonacular.com/italian-pasta-salad-with-organic-arugula-648190"),
    isFavorite = false,
    isLoading = false,
    onFavoriteTapped = null,
    ingredients = listOf(
        "1/4 pound organic arugula remove steam",
        "1 can Goya Chick Peas",
        "1 cup of Botticelli Extra Virgin Olive Oil Botticelli Extra Virgin Olive Oil",
        "1 pound Barilla Piccolini mini Farfalle",
        "1 tablespoon Fresh Basil",
        "juice and rind of 1 lemon",
        "tablespoon Fresh oregano",
        "1 cup pecorino romano cheese",
        "Salt and Pepper",
        "1 cup diced sundried tomatoes"
    )
)
