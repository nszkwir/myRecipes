package com.spitzer.data.repository

import android.database.sqlite.SQLiteException
import com.spitzer.contracts.RecipeRepository
import com.spitzer.data.di.AppDispatchers
import com.spitzer.data.di.Dispatcher
import com.spitzer.data.remote.api.recipe.RecipeService
import com.spitzer.data.repository.mapper.RecipeDetailsMapper.mapFromRecipeDetailsResponse
import com.spitzer.data.repository.mapper.RecipeDetailsMapper.mapFromStoredRecipeDetails
import com.spitzer.data.repository.mapper.RecipeDetailsMapper.mapToStoredRecipeDetails
import com.spitzer.data.repository.mapper.RecipeMapper.mapFromRecipePageResponse
import com.spitzer.data.repository.mapper.RecipeMapper.mapFromRecipeResponse
import com.spitzer.data.repository.mapper.RecipeMapper.mapFromStoredRecipes
import com.spitzer.data.repository.mapper.RecipeMapper.mapSortCriteria
import com.spitzer.data.repository.mapper.RecipeMapper.mapSortOrder
import com.spitzer.data.repository.mapper.RecipeMapper.mapToStoredRecipes
import com.spitzer.data.storage.database.room.dao.FavoriteRecipeDao
import com.spitzer.data.storage.database.room.dao.RecipeDetailsDao
import com.spitzer.data.storage.database.room.dao.RecipesDao
import com.spitzer.data.storage.database.room.dto.StoredFavoriteRecipe
import com.spitzer.data.storage.sharedpreferences.RecipeSharedPreferences
import com.spitzer.entity.recipe.Recipe
import com.spitzer.entity.recipe.RecipeDetails
import com.spitzer.entity.recipe.RecipePage
import com.spitzer.entity.search.SearchCriteria
import com.spitzer.entity.search.SortCriteria
import com.spitzer.entity.search.SortOrder
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import kotlin.math.min

class RecipeRepositoryImpl @Inject constructor(
    private val recipeService: RecipeService,
    private val recipesDao: RecipesDao,
    private val recipeDetailsDao: RecipeDetailsDao,
    private val favoriteRecipeDao: FavoriteRecipeDao,
    private val recipePreferences: RecipeSharedPreferences,
    @Dispatcher(AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
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
     *  Thread safe fetching from DB on runtime instantiation.
     */
    init {
        CoroutineScope(SupervisorJob() + ioDispatcher).launch {
            try {
                searchMutex.withLock {
                    val recipes = recipesDao.get()

                    // Loading favorite indexes
                    favoriteRecipes = favoriteRecipeDao.get()
                        .associateBy({ it.id }, { it.favorite }).toMutableMap()

                    // Getting latest totalResults from SharedPreferences
                    val totalResults = recipePreferences.getRecipeListTotalResults()

                    // Mapping and allocating
                    val recipeList: MutableList<Recipe?> =
                        MutableList(totalResults, init = { null })
                    mapFromStoredRecipes(recipes).forEachIndexed { index, recipe ->
                        recipeList[index] = recipe.copy(
                            isFavorite = favoriteRecipes[recipe.id] ?: false
                        )
                    }
                    _recipePage.value = RecipePage(
                        list = recipeList,
                        totalResults = totalResults
                    )

                    // Updating offset
                    recipePageCurrentOffset = recipes.count()
                }
            } catch (e: IllegalStateException) {
                initializeEmptyRecipes()
            } catch (e: SQLiteException) {
                initializeEmptyRecipes()
            } catch (e: IndexOutOfBoundsException) {
                initializeEmptyRecipes()
            } catch (e: ClassCastException) {
                initializeEmptyRecipes()
            }
        }
    }

    private fun initializeEmptyRecipes() {
        _recipePage.value = RecipePage(
            list = mutableListOf(),
            totalResults = 0
        )
        recipePageCurrentOffset = 0
    }

    /**
     *  Updates the favorite value for a recipe.
     *  The update is performed in the local favoriteRecipes map, on database.
     *  On our main recipe list state _recipePage, we iterate also to update the recipe favorite value.
     */
    override suspend fun setRecipeFavorite(id: Long, isFavorite: Boolean) {
        favoriteRecipeDao.upsert(StoredFavoriteRecipe(id, isFavorite))
        favoriteRecipes[id] = isFavorite
        val recipeIndex = _recipePage.value.list.indexOfFirst {
            it?.id == id
        }
        if (recipeIndex == -1) {
            return
        }
        val mutableList = _recipePage.value.list.toMutableList()
        mutableList[recipeIndex] = mutableList[recipeIndex]?.copy(isFavorite = isFavorite)
        _recipePage.update { currentState ->
            currentState.copy(list = mutableList)
        }
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
                    recipesDao.deleteAll()
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

            recipePreferences.updateRecipeListTotalResults(maximumResults)

            recipesDao.upsert(
                mapToStoredRecipes(recipes)
            )

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
        searchMutex.withLock {
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
        }
    }


    /**
     *  Get the recipeDetails by recipe id.
     */
    override suspend fun getRecipeDetailsById(id: Long): RecipeDetails {
        // Search the recipe details on database
        val recipeDetails = recipeDetailsDao.getRecipeById(id)
        val isFavorite = favoriteRecipes[id] ?: false

        return if (recipeDetails != null)
            mapFromStoredRecipeDetails(recipeDetails)
                .copy(isFavorite = isFavorite)
        else {
            // If not found on database, will fetch from
            // remote and update the result on database.
            val response = recipeService.fetchRecipeDetails(id)
            val refreshedRecipeDetails = mapFromRecipeDetailsResponse(response)

            recipeDetailsDao.upsert(
                mapToStoredRecipeDetails(refreshedRecipeDetails)
            )
            refreshedRecipeDetails.copy(isFavorite = isFavorite)
        }
    }

    /**
     *  Fetches the recipeDetails by recipe id from remote.
     */
    override suspend fun fetchRecipeDetails(id: Long): RecipeDetails {
        val response = recipeService.fetchRecipeDetails(id)
        val isFavorite = favoriteRecipes[id] ?: false
        val recipeDetails = mapFromRecipeDetailsResponse(response).copy(isFavorite = isFavorite)
        // After fetching from remote, upsert into database
        recipeDetailsDao.upsert(
            mapToStoredRecipeDetails(recipeDetails)
        ).also {
            return recipeDetails
        }
    }
}
