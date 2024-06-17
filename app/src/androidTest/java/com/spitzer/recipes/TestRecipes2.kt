package com.spitzer.recipes

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipe
import com.spitzer.recipe.R
import com.spitzer.designsystem.R as Rds
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
 *  TestRecipes2
 *  Goal: assert that the pull to refresh works in RecipeListScreen, and when refresh from remote fails,
 *  ErrorScreenView is displayed with it's two buttons Retry and Cancel. Assert that both buttons
 *  perform their tasks.
 *  Details: total recipes in remote 20. To validate elements the recipes names follows the
 *  pattern "Recipe %n" where n is the recipe index (base 1).
 *  Flow description:
 *      - app startup at RecipeListScreen
 *      - fetches from remote the first 15 recipes
 *      - validates element with text "Recipe 1" is shown
 *      - validates element with text "Recipe 16" does not exist
 *      - pull to refresh fails
 *      - validates that ErrorScreenView is displayed
 *      - perform click on Retry Button in ErrorScreenView
 *      - fetches from remote the first 15 recipes
 *      - validates element with text "Recipe 1" is shown
 *      - validates element with text "Recipe 16" does not exist
 *      - pull to refresh succeeds
 *      - validates element with text "Recipe 1" is shown
 *      - validates element with text "Recipe 16" does not exist
 *      - pull to refresh fails
 *      - validates that ErrorScreenView is displayed
 *      - perform click on Cancel Button in ErrorScreenView
 *      - validates element with text "Recipe 1" is shown
 *      - validates element with text "Recipe 16" does not exist
 */
@OptIn(androidx.compose.ui.test.ExperimentalTestApi::class)
@HiltAndroidTest
class TestRecipes2 : BaseUiTest() {

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
    }

    @After
    fun clear() {
        shutDownServer()
    }

    @Test
    fun test() {
        hiltRule.inject()
        val recipeListTag = composeRule.activity.getString(R.string.recipeList_TestTag)
        val errorViewTag = composeRule.activity.getString(Rds.string.errorView_TestTag)
        val retryButtonTag = composeRule.activity.getString(Rds.string.errorView_RetryButton_TestTag)
        val cancelButtonTag = composeRule.activity.getString(Rds.string.errorView_CancelButton_TestTag)

        with(composeRule) {
            sleep(500)
            onNodeWithText("Recipe 1").assertIsDisplayed()
            onNodeWithText("Recipe 16").assertDoesNotExist()

            removeResponse(
                pathPattern = "/recipes/complexSearch?offset=0&number=15&addRecipeInformation=true&sortDirection=desc",
            )
            sleep(200)
            onNodeWithTag(recipeListTag).assertIsDisplayed()
                .performTouchInput {
                    swipe(
                        start = center,
                        end = center + Offset(x = 0f, y = 700f),
                        durationMillis = 1000,
                    )
                }
            sleep(500)
            onNodeWithText("Recipe 1").assertDoesNotExist()
            onNodeWithTag(errorViewTag).assertIsDisplayed()
            sleep(500)

            addResponse(
                pathPattern = "/recipes/complexSearch?offset=0&number=15&addRecipeInformation=true&sortDirection=desc",
                filename = "test_recipes_1_response_1.json",
            )
            sleep(200)
            onNodeWithTag(retryButtonTag).assertIsDisplayed()
                .performClick()
            sleep(500)
            onNodeWithText("Recipe 1").assertIsDisplayed()
            onNodeWithText("Recipe 16").assertDoesNotExist()
            sleep(500)


            onNodeWithTag(recipeListTag).assertIsDisplayed()
                .performTouchInput {
                    swipe(
                        start = center,
                        end = center + Offset(x = 0f, y = 700f),
                        durationMillis = 1000,
                    )
                }
            sleep(500)
            onNodeWithText("Recipe 1").assertIsDisplayed()
            onNodeWithText("Recipe 16").assertDoesNotExist()
            sleep(500)

            removeResponse(
                pathPattern = "/recipes/complexSearch?offset=0&number=15&addRecipeInformation=true&sortDirection=desc",
            )
            sleep(200)
            onNodeWithTag(recipeListTag).assertIsDisplayed()
                .performTouchInput {
                    swipe(
                        start = center,
                        end = center + Offset(x = 0f, y = 700f),
                        durationMillis = 1000,
                    )
                }
            sleep(500)
            onNodeWithText("Recipe 1").assertDoesNotExist()
            onNodeWithTag(errorViewTag).assertIsDisplayed()
            onNodeWithTag(cancelButtonTag).assertIsDisplayed()
                .performClick()
            sleep(200)
            onNodeWithText("Recipe 1").assertIsDisplayed()
            onNodeWithText("Recipe 16").assertDoesNotExist()
        }
    }
}
