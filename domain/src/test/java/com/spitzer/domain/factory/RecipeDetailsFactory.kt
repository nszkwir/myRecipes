package com.spitzer.domain.factory

import com.spitzer.entity.recipe.RecipeDetails

object RecipeDetailsFactory {

    fun getRecipeDetails(
        id: Long = 888123821,
        imageUrl: String? = "https://spoonacular.com/italian-pasta-salad-with-organic-arugula-648190",
        sourceUrl: String? = "https://spoonacular.com/italian-pasta-salad-with-organic-arugula-648190",
    ) = RecipeDetails(
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
        image = null,
        healthScore = 10,
        diets = emptyList(),
        spoonacularScore = 4.5,
        spoonacularSourceUrl = null,
        ingredients = listOf(
            "Pasta",
            "Tomato sauce"
        ),
        isFavorite = false
    )
}
