package com.spitzer.recipes

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollToIndex
import com.spitzer.recipe.R
import com.spitzer.recipes.main.MainActivity
import com.spitzer.utils.BaseUiTest
import com.spitzer.utils.sleep
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

/**
 *  TestRecipes1
 *  Goal: fetch 2 pages of recipes and validate the elements through the flow in RecipeListScreen
 *  Details: total recipes in remote 20. To validate elements the recipes names follows the
 *  pattern "Recipe %n" where n is the recipe index (base 1)
 *  Flow description:
 *      - app startup at RecipeListScreen
 *      - fetches from remote the first 15 recipes
 *      - validates element with text "Recipe 1" is shown
 *      - validates element with text "Recipe 16" does not exist
 *      - scrolls the list down to element at index 14
 *      - fetches from remote the next recipes (5 more as 20 is the max amount)
 *      - scrolls the list down to element at index 19
 *      - validates element with text "Recipe 20" is shown
 *      - validates element with text "Recipe 21" does not exist
 */
@OptIn(androidx.compose.ui.test.ExperimentalTestApi::class)
@HiltAndroidTest
class TestRecipes1 : BaseUiTest() {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @BindValue
    @get:Rule(order = 1)
    val tmpFolder: TemporaryFolder = TemporaryFolder.builder().assureDeletion().build()

    @get:Rule(order = 2)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        startMockServer()
        addResponse(
            pathPattern = "/recipes/complexSearch?offset=0&number=15&addRecipeInformation=true&sortDirection=desc",
            filename = "test_recipes_1_response_1.json",
        )
        addResponse(
            pathPattern = "/recipes/complexSearch?offset=15&number=15&addRecipeInformation=true&sortDirection=desc",
            filename = "test_recipes_1_response_2.json",
        )
    }

    @After
    fun clear() {
        shutDownServer()
    }

    @Test
    fun test() {
        hiltRule.inject()
        val recipeListTag = composeRule.activity.getString(R.string.recipeList_TestTag)

        with(composeRule) {
            sleep(500)
            onNodeWithText("Recipe 1").assertIsDisplayed()
            onNodeWithText("Recipe 16").assertDoesNotExist()
            onNodeWithTag(recipeListTag).performScrollToIndex(14)
            sleep(500)
            onNodeWithTag(recipeListTag).performScrollToIndex(19)
            sleep(500)
            onNodeWithText("Recipe 20").assertIsDisplayed()
            onNodeWithText("Recipe 21").assertDoesNotExist()
        }
    }
}
