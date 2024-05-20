package com.spitzer.data.factory

import com.spitzer.data.remote.api.recipe.dto.RecipePageResponse
import com.spitzer.data.remote.api.recipe.dto.RecipeResponse
import com.spitzer.data.storage.database.room.dto.StoredRecipe

object RecipeFactory {
    fun getRecipePageResponse(
        totalResults: Int = 5000,
        firstRecipeId: Long = 0,
        recipesAmount: Int = 15,
    ) = RecipePageResponse(
        totalResults = totalResults,
        results = getListOfRecipeResponse(firstRecipeId, recipesAmount)
    )

    fun getListOfRecipeResponse(
        firstRecipeId: Long,
        recipesAmount: Int,
    ): List<RecipeResponse> {
        val list: MutableList<RecipeResponse?> = MutableList(recipesAmount) { null }
        for (i in 0..<recipesAmount) {
            list[i] = getRecipeResponse(id = firstRecipeId + i)
        }
        return list.mapNotNull { it }
    }

    fun getRecipeResponse(
        id: Long,
    ): RecipeResponse {
        return RecipeResponse(
            id = id,
            title = "Title",
            summary = "Summary",
            image = "Image"
        )
    }

    fun getListOfStoredRecipe(
        firstRecipeId: Long = 0,
        recipesAmount: Int = 15,
    ): List<StoredRecipe> {
        if (recipesAmount < 1) return emptyList()
        val list: MutableList<StoredRecipe?> = MutableList(recipesAmount) { null }
        for (i in 0..<recipesAmount) {
            list[i] = getStoredRecipe(id = firstRecipeId + i)
        }
        return list.mapNotNull { it }
    }

    fun getStoredRecipe(
        id: Long,
    ): StoredRecipe {
        return StoredRecipe(
            id = id,
            title = "Title",
            summary = "Summary",
            image = "Image"
        )
    }

}
