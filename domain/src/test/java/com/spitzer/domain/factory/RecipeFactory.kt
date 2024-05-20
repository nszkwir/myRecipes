package com.spitzer.domain.factory

import com.spitzer.entity.recipe.Recipe

object RecipeFactory {
    fun getRecipeList(
        recipesAmount: Int = 10,
        firstRecipeId: Long = 100L,
    ): List<Recipe> {
        val list: MutableList<Recipe?> = MutableList(recipesAmount) { null }
        for (i in 0..<recipesAmount) {
            list[i] = getRecipe(recipeId = firstRecipeId + i)
        }
        return list.mapNotNull { it }
    }

    fun getRecipe(
        recipeId: Long,
    ) = Recipe(
        id = recipeId,
        title = "Title",
        summary = "Summary",
        image = null,
        isFavorite = false
    )
}
