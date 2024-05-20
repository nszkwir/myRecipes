package com.spitzer.screenshot.designSystem.component.preview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.spitzer.designsystem.components.HTMLTextView
import com.spitzer.designsystem.components.LinkHTMLTextView
import com.spitzer.designsystem.theme.RTheme

@Composable
fun HTMLTextViewPreview() {
    val text1 =
        "One portion of this dish contains approximately <b>28g of protein</b>, <b>20g of fat</b>, and a total of <b>696 calories</b>. For <b>\$3.84 per serving</b>, you get a main course that serves 4. 1 person has tried and liked this recipe. If you like this recipe, take a look at these similar recipes: <a href=\\\"https://spoonacular.com/recipes/weight-watchers-italian-arugula-salad-525364\\\">Weight Watchers Italian Arugula Salad</a>, <a href=\\\"https://spoonacular.com/recipes/arugula-italian-tuna-and-white-bean-salad-7940\\\">Arugula, Italian Tuna, and White Bean Salad</a>, and <a href=\\\"https://spoonacular.com/recipes/arugula-pasta-salad-547702\\\">Arugula Pasta Salad</a>.\""
    val text2 = "https://spoonacular.com/italian-pasta-salad-with-organic-arugula-648190"
    RTheme {
        Surface(color = RTheme.colors.n99n00) {
            Column(
                modifier = Modifier.padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                HTMLTextView(
                    text = text1,
                    color = RTheme.colors.n20n80,
                    style = RTheme.typography.body2,
                )

                LinkHTMLTextView(
                    urlString = text2,
                    color = RTheme.colors.n20n80,
                    style = RTheme.typography.body2,
                )

                LinkHTMLTextView(
                    text = "www.google.com",
                    urlString = text2,
                    color = RTheme.colors.n20n80,
                    style = RTheme.typography.body2,
                )
            }
        }
    }
}
