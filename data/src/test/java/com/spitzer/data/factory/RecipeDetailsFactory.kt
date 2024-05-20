package com.spitzer.data.factory

import com.spitzer.data.remote.api.recipe.dto.RecipeDetailsResponse
import com.spitzer.data.storage.database.room.dto.StoredRecipeDetails

object RecipeDetailsFactory {
    fun getRecipeDetailsResponse(
        id: Long = 888123821,
        imageUrl: String? = "https://spoonacular.com/italian-pasta-salad-with-organic-arugula-648190",
        sourceUrl: String? = "https://spoonacular.com/italian-pasta-salad-with-organic-arugula-648190",
    ) = RecipeDetailsResponse(
        id = id,
        title = "Italian Pasta Salad with organic Arugula",
        readyInMinutes = 15,
        servings = 4,
        summary = "The recipe Italian Pasta Salad with organic Arugula could satisfy your Mediterranean craving in approximately <b>45 minutes</b>. ",
        instructions = "<ol><li>Boil Pasta</li><li>Meanwhile in a pasta bowl add arugala, sundried tomatoes, ...",
        vegetarian = true,
        vegan = true,
        glutenFree = false,
        dairyFree = false,
        image = imageUrl,
        healthScore = 10,
        diets = emptyList(),
        spoonacularScore = 4.5,
        spoonacularSourceUrl = sourceUrl,
        extendedIngredients = listOf(
            RecipeDetailsResponse.Ingredient("Pasta", null),
            RecipeDetailsResponse.Ingredient("Tomato sauce", null)
        )
    )

    fun getStoredRecipeDetails(
        id: Long = 888123821,
        imageUrl: String? = "https://spoonacular.com/italian-pasta-salad-with-organic-arugula-648190",
        sourceUrl: String? = "https://spoonacular.com/italian-pasta-salad-with-organic-arugula-648190",
    ) = StoredRecipeDetails(
        id = id,
        title = "Italian Pasta Salad with organic Arugula",
        readyInMinutes = 15,
        servings = 4,
        summary = "The recipe Italian Pasta Salad with organic Arugula could satisfy your Mediterranean craving in approximately <b>45 minutes</b>. ",
        instructions = "<ol><li>Boil Pasta</li><li>Meanwhile in a pasta bowl add arugala, sundried tomatoes, ...",
        vegetarian = true,
        vegan = true,
        glutenFree = false,
        dairyFree = false,
        image = imageUrl,
        healthScore = 10,
        diets = emptyList(),
        spoonacularScore = 4.5,
        spoonacularSourceUrl = sourceUrl,
        ingredients = listOf(
            "Pasta",
            "Tomato sauce"
        )
    )
}
