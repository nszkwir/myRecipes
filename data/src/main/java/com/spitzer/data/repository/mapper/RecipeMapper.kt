package com.spitzer.data.repository.mapper

import com.spitzer.data.remote.api.recipe.dto.RecipePageResponse
import com.spitzer.data.remote.api.recipe.dto.RecipeResponse
import com.spitzer.data.storage.database.room.dto.StoredRecipe
import com.spitzer.entity.recipe.Recipe
import com.spitzer.entity.recipe.RecipePage
import com.spitzer.entity.search.SortCriteria
import com.spitzer.entity.search.SortOrder
import java.net.MalformedURLException
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

    fun mapFromStoredRecipes(storedRecipes: List<StoredRecipe>): List<Recipe> {
        return storedRecipes.map {
            mapFromStoredRecipe(it)
        }
    }

    private fun mapFromStoredRecipe(storedRecipe: StoredRecipe): Recipe {
        return Recipe(
            id = storedRecipe.id,
            title = storedRecipe.title,
            image = parseUrl(storedRecipe.image),
            summary = storedRecipe.summary,
            isFavorite = false
        )
    }

    fun mapToStoredRecipes(recipes: List<Recipe?>): List<StoredRecipe> {
        return recipes.mapNotNull {
            mapToStoredRecipe(it)
        }
    }

    private fun mapToStoredRecipe(recipe: Recipe?): StoredRecipe? {
        if (recipe == null) return null
        return StoredRecipe(
            id = recipe.id,
            title = recipe.title,
            image = recipe.image?.toString() ?: "",
            summary = recipe.summary,
        )
    }

    fun mapSortCriteria(sortCriteria: SortCriteria): String? {
        return when (sortCriteria) {
            SortCriteria.RELEVANCE -> null
            SortCriteria.POPULARITY -> "popularity"
            SortCriteria.PREPARATION_TIME -> "time"
            SortCriteria.CALORIES -> "calories"
        }
    }

    fun mapSortOrder(sortOrder: SortOrder): String = when (sortOrder) {
        SortOrder.ASCENDING -> "asc"
        SortOrder.DESCENDING -> "desc"
    }


    private fun parseUrl(urlString: String?): URL? = try {
        URL(urlString)
    } catch (e: MalformedURLException) {
        null
    }
}

fun String?.parseToUrl2() = try {
    URL(this)
} catch (e: MalformedURLException) {
    null
}

