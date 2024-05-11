package com.spitzer.data.repository.mapper

import com.spitzer.data.remote.api.recipe.dto.RecipePageResponse
import com.spitzer.data.remote.api.recipe.dto.RecipeResponse
import com.spitzer.entity.recipe.Recipe
import com.spitzer.entity.recipe.RecipePage
import java.net.URL

object RecipeMapper {

    fun mapFromRecipePageResponse(response: RecipePageResponse): RecipePage {
        return RecipePage(
            list = response.results.map {
                mapFromRecipeResponse(it)
            }.toMutableList(),
            totalResults = response.totalResults
        )
    }

    fun mapFromRecipeResponse(response: RecipeResponse): Recipe {
        return Recipe(
            id = response.id,
            title = response.title,
            image = parseUrl(response.image),
            summary = response.summary,
            isFavorite = false
        )
    }

    private fun parseUrl(urlString: String?): URL? {
        return try {
            URL(urlString)
        } catch (e: Exception) {
            null
        }
    }
}
