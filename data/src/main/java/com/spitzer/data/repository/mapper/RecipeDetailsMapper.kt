package com.spitzer.data.repository.mapper

import com.spitzer.data.remote.api.recipe.dto.RecipeDetailsResponse
import com.spitzer.entity.recipe.RecipeDetails
import java.net.URL

object RecipeDetailsMapper {
    fun mapFromRecipeDetailsResponse(response: RecipeDetailsResponse): RecipeDetails {
        return RecipeDetails(
            id = response.id,
            title = response.title,
            isFavorite = false,
            readyInMinutes = response.readyInMinutes,
            servings = response.servings,
            summary = response.summary,
            instructions = response.instructions,
            vegetarian = response.vegetarian,
            vegan = response.vegan,
            glutenFree = response.glutenFree,
            dairyFree = response.dairyFree,
            image = parseUrl(response.image),
            healthScore = response.healthScore,
            diets = response.diets ?: emptyList(),
            spoonacularScore = response.spoonacularScore,
            spoonacularSourceUrl = parseUrl(response.spoonacularSourceUrl),
            ingredients = response.extendedIngredients?.mapNotNull { it.original } ?: emptyList()
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
