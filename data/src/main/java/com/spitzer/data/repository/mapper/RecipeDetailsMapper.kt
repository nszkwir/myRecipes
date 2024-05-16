package com.spitzer.data.repository.mapper

import com.spitzer.data.remote.api.recipe.dto.RecipeDetailsResponse
import com.spitzer.data.storage.database.room.dto.StoredRecipeDetails
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

    fun mapFromStoredRecipeDetails(storedRecipeDetails: StoredRecipeDetails): RecipeDetails {
        return RecipeDetails(
            id = storedRecipeDetails.id,
            title = storedRecipeDetails.title,
            isFavorite = false,
            readyInMinutes = storedRecipeDetails.readyInMinutes,
            servings = storedRecipeDetails.servings,
            summary = storedRecipeDetails.summary,
            instructions = storedRecipeDetails.instructions,
            vegetarian = storedRecipeDetails.vegetarian,
            vegan = storedRecipeDetails.vegan,
            glutenFree = storedRecipeDetails.glutenFree,
            dairyFree = storedRecipeDetails.dairyFree,
            image = parseUrl(storedRecipeDetails.image),
            healthScore = storedRecipeDetails.healthScore,
            diets = storedRecipeDetails.diets,
            spoonacularScore = storedRecipeDetails.spoonacularScore,
            spoonacularSourceUrl = parseUrl(storedRecipeDetails.spoonacularSourceUrl),
            ingredients = storedRecipeDetails.ingredients
        )
    }

    fun mapToStoredRecipeDetails(recipeDetails: RecipeDetails): StoredRecipeDetails {
        return StoredRecipeDetails(
            id = recipeDetails.id,
            title = recipeDetails.title,
            readyInMinutes = recipeDetails.readyInMinutes,
            servings = recipeDetails.servings,
            summary = recipeDetails.summary,
            instructions = recipeDetails.instructions,
            vegetarian = recipeDetails.vegetarian,
            vegan = recipeDetails.vegan,
            glutenFree = recipeDetails.glutenFree,
            dairyFree = recipeDetails.dairyFree,
            image = recipeDetails.image?.toString() ?: "",
            healthScore = recipeDetails.healthScore,
            diets = recipeDetails.diets ?: emptyList(),
            spoonacularScore = recipeDetails.spoonacularScore,
            spoonacularSourceUrl = recipeDetails.spoonacularSourceUrl?.toString() ?: "",
            ingredients = recipeDetails.ingredients
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
