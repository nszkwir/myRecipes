package com.spitzer.contracts

import com.spitzer.entity.recipe.Recipe
import com.spitzer.entity.recipe.RecipeDetails
import com.spitzer.entity.recipe.RecipePage
import com.spitzer.entity.search.SearchCriteria
import com.spitzer.entity.search.SortCriteria
import com.spitzer.entity.search.SortOrder
import kotlinx.coroutines.flow.StateFlow

interface RecipeRepository {
    val recipePage: StateFlow<RecipePage>
    suspend fun setRecipeFavorite(id: Long, isFavorite: Boolean)
    suspend fun refreshRecipeList(sortCriteria: SortCriteria, sortOrder: SortOrder)
    suspend fun fetchRecipeList(elementIndex: Int, sortCriteria: SortCriteria, sortOrder: SortOrder)
    suspend fun searchRecipeList(
        query: String,
        searchCriteria: SearchCriteria,
        sortCriteria: SortCriteria,
        sortOrder: SortOrder
    ): List<Recipe>

    suspend fun getRecipeDetailsById(id: Long): RecipeDetails
    suspend fun fetchRecipeDetails(id: Long): RecipeDetails
}