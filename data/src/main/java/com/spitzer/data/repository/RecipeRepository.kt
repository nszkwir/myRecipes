package com.spitzer.data.repository

import com.spitzer.contracts.RecipeRepository
import com.spitzer.data.remote.api.recipe.RecipeService
import com.spitzer.data.repository.mapper.RecipeMapper.mapFromRecipePageResponse
import com.spitzer.data.repository.mapper.RecipeMapper.mapFromRecipeResponse
import com.spitzer.data.repository.mapper.RecipeMapper.mapSortCriteria
import com.spitzer.data.repository.mapper.RecipeMapper.mapSortOrder
import com.spitzer.entity.recipe.Recipe
import com.spitzer.entity.recipe.RecipePage
import com.spitzer.entity.search.SearchCriteria
import com.spitzer.entity.search.SortCriteria
import com.spitzer.entity.search.SortOrder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import javax.inject.Inject
import kotlin.math.min

class RecipeRepositoryImpl @Inject constructor(
    private val recipeService: RecipeService
) : RecipeRepository {

    companion object {
        const val maximumPageOffset = 900
        const val itemsPerPageLimit = 15
    }

    private var searchMutex = Mutex()
    private var recipePageCurrentOffset = 0

    private var favoriteRecipes: MutableMap<Long, Boolean> = mutableMapOf()

    private val _recipePage by lazy {
        MutableStateFlow(
            RecipePage(mutableListOf(), 0)
        )
    }
    override val recipePage: StateFlow<RecipePage> by lazy {
        _recipePage.asStateFlow()
    }


    /**
     *  Fetches the first recipe page.
     *  Set the recipePageCurrentOffset = 0. This way the page to fetch is the first one.
     */
    override suspend fun refreshRecipeList(sortCriteria: SortCriteria, sortOrder: SortOrder) {
        recipePageCurrentOffset = 0
        fetchRecipeList(elementIndex = 0, sortCriteria = sortCriteria, sortOrder = sortOrder)
    }

    /**
     *  Fetches the next recipes page if needed.
     *  According to the index, fetch the next page recipe and update the database.
     */
    override suspend fun fetchRecipeList(
        elementIndex: Int,
        sortCriteria: SortCriteria,
        sortOrder: SortOrder
    ) {
        searchMutex.lock()

        // Free the mutex if should not fetch according to current elementIndex
        val shouldLoadNextPage = elementIndex >= recipePageCurrentOffset
        if (!shouldLoadNextPage) {
            searchMutex.unlock()
            return
        }

        try {
            val response = recipeService.fetchRecipes(
                offset = elementIndex,
                limit = itemsPerPageLimit,
                query = null,
                includeIngredients = null,
                sortCriteria = mapSortCriteria(sortCriteria),
                sortOrder = mapSortOrder(sortOrder)
            )

            // Validation due to API limitation on offset max value
            val maximumResults = min(maximumPageOffset + itemsPerPageLimit, response.totalResults)
            // Calculate how many items we will take from response
            val remainder = min(maximumResults - elementIndex, response.results.count())
            recipePageCurrentOffset += remainder
            searchMutex.unlock()

            if (response.results.isEmpty()) {
                return
            }

            val recipeList: MutableList<Recipe?> =
                // This means a refresh was forced. We clear our recipes table.
                if (elementIndex == 0) {
                    MutableList(maximumResults, init = { null })
                } else {
                    _recipePage.value.list.toMutableList()
                }

            val recipePage = mapFromRecipePageResponse(response)
            // Taking the correct amount of recipes
            val recipes = recipePage.list.take(remainder).map {
                it?.copy(isFavorite = favoriteRecipes[it.id] ?: false)
            }

            recipes.forEachIndexed { index, recipe ->
                recipeList[elementIndex + index] = recipe
            }

            _recipePage.update { currentState ->
                currentState.copy(list = recipeList, totalResults = maximumResults)
            }
        } finally {
            if (searchMutex.isLocked) {
                searchMutex.unlock()
            }
        }
    }

    /**
     *  Request the recipes search by criteria.
     */
    override suspend fun searchRecipeList(
        query: String,
        searchCriteria: SearchCriteria,
        sortCriteria: SortCriteria,
        sortOrder: SortOrder
    ): List<Recipe> {
        searchMutex.lock()

        try {
            val searchByName = if (searchCriteria == SearchCriteria.NAME) query else null
            val searchByIngredients = if (searchCriteria == SearchCriteria.INGREDIENTS) {
                query.split("[\\s,]+".toRegex()).joinToString(",")
            } else null
            val response = recipeService.fetchRecipes(
                offset = 0,
                limit = 20,
                query = searchByName,
                includeIngredients = searchByIngredients,
                sortCriteria = mapSortCriteria(sortCriteria),
                sortOrder = mapSortOrder(sortOrder)
            )
            return response.results.map {
                mapFromRecipeResponse(it).copy(
                    isFavorite = favoriteRecipes[it.id] ?: false
                )
            }
        } finally {
            searchMutex.unlock()
        }
    }

}
