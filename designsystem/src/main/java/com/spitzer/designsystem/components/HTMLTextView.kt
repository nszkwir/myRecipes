package com.spitzer.designsystem.components

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.text.TextUtils
import android.util.TypedValue
import android.widget.TextView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import com.spitzer.designsystem.extensions.toTypeface
import com.spitzer.designsystem.theme.RTheme


@Composable
fun HTMLTextView(
    modifier: Modifier = Modifier,
    text: String,
    color: Color,
    style: TextStyle,
    fontWeight: FontWeight? = null,
    maxLines: Int = Integer.MAX_VALUE,
) {
    val typeface = style.toTypeface(fontWeight = fontWeight)
    val letterSpacingInPixels = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        style.letterSpacing.value,
        LocalContext.current.resources.displayMetrics
    )
    AndroidView(
        factory = { context ->
            TextView(context).apply {
                this.text = HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY)
                this.typeface = typeface
                this.letterSpacing = letterSpacingInPixels / this.textSize
                this.textSize = style.fontSize.value
                this.maxLines = maxLines
                this.ellipsize = TextUtils.TruncateAt.END
                this.setTextColor(color.toArgb())
            }
        },
        modifier = modifier
    )
}

@Composable
fun LinkHTMLTextView(
    modifier: Modifier = Modifier,
    text: String? = null,
    urlString: String,
    color: Color = Color.Blue,
    style: TextStyle = RTheme.typography.body2,
    fontWeight: FontWeight? = null,
    underlined: Boolean = true,
    maxLines: Int = Integer.MAX_VALUE,
) {
    val displayText = try {
        text ?: urlString
    } catch (e: Exception) {
        ""
    }
    val context = LocalContext.current
    HTMLTextView(
        modifier = modifier.clickable {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlString))
            context.startActivity(intent)
        },
        text = if (underlined) "<u>$displayText</u>" else displayText,
        color = color,
        style = style,
        fontWeight = fontWeight,
        maxLines = maxLines
    )
}

@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Light Mode")
@Composable
fun PreviewHTMLTextView() {
    val text =
        "\"The recipe Italian Pasta Salad with organic Arugula could satisfy your Mediterranean craving in approximately <b>45 minutes</b>. One portion of this dish contains approximately <b>28g of protein</b>, <b>20g of fat</b>, and a total of <b>696 calories</b>. For <b>\$3.84 per serving</b>, you get a main course that serves 4. 1 person has tried and liked this recipe. It is brought to you by Foodista. A mixture of pecorino romano cheese, oregano, botticelli extra virgin olive oil, and a handful of other ingredients are all it takes to make this recipe so flavorful. Taking all factors into account, this recipe <b>earns a spoonacular score of 85%</b>, which is amazing. If you like this recipe, take a look at these similar recipes: <a href=\\\"https://spoonacular.com/recipes/weight-watchers-italian-arugula-salad-525364\\\">Weight Watchers Italian Arugula Salad</a>, <a href=\\\"https://spoonacular.com/recipes/arugula-italian-tuna-and-white-bean-salad-7940\\\">Arugula, Italian Tuna, and White Bean Salad</a>, and <a href=\\\"https://spoonacular.com/recipes/arugula-pasta-salad-547702\\\">Arugula Pasta Salad</a>.\""
    RTheme {
        Surface(color = RTheme.colors.n99n00) {
            Column(
                modifier = Modifier.padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                HTMLTextView(
                    text = text,
                    color = RTheme.colors.n20n80,
                    style = RTheme.typography.body2,
                )
            }
        }
    }
}

@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Light Mode")
@Composable
fun PreviewLinkHTMLTextView() {
    val text = "https://spoonacular.com/italian-pasta-salad-with-organic-arugula-648190"
    RTheme {
        Surface(color = RTheme.colors.n99n00) {
            Column(
                modifier = Modifier.padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                LinkHTMLTextView(
                    urlString = text,
                    color = RTheme.colors.n20n80,
                    style = RTheme.typography.body2,
                )

                LinkHTMLTextView(
                    text = "www.google.com",
                    urlString = text,
                    color = RTheme.colors.n20n80,
                    style = RTheme.typography.body2,
                )
            }
        }
    }
}
