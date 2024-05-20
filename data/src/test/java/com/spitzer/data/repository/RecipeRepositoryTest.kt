package com.spitzer.data.repository

import com.spitzer.contracts.RecipeRepository
import com.spitzer.data.factory.RecipeDetailsFactory
import com.spitzer.data.factory.RecipeFactory
import com.spitzer.data.remote.api.recipe.RecipeService
import com.spitzer.data.repository.mapper.RecipeDetailsMapper
import com.spitzer.data.repository.mapper.RecipeMapper
import com.spitzer.data.storage.database.room.dao.FavoriteRecipeDao
import com.spitzer.data.storage.database.room.dao.RecipeDetailsDao
import com.spitzer.data.storage.database.room.dao.RecipesDao
import com.spitzer.data.storage.database.room.dto.StoredFavoriteRecipe
import com.spitzer.data.storage.database.room.dto.StoredRecipe
import com.spitzer.data.storage.sharedpreferences.RecipeSharedPreferences
import com.spitzer.entity.recipe.Recipe
import com.spitzer.entity.recipe.RecipePage
import com.spitzer.entity.search.SearchCriteria
import com.spitzer.entity.search.SortCriteria
import com.spitzer.entity.search.SortOrder
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNotEquals
import org.junit.Test
import kotlin.math.min

class RecipeRepositoryTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    private val testScope = TestScope(UnconfinedTestDispatcher())

    private lateinit var subject: RecipeRepository

    private val recipeService = mockk<RecipeService>(relaxed = true)
    private val recipesDao = mockk<RecipesDao>(relaxed = true)
    private val recipeDetailsDao = mockk<RecipeDetailsDao>(relaxed = true)
    private val favoriteRecipeDao = mockk<FavoriteRecipeDao>(relaxed = true)
    private val recipePreferences = mockk<RecipeSharedPreferences>(relaxed = true)

    private val exceptionMessage = "Something went wrong"

    private var favoriteRecipes: MutableMap<Long, Boolean> = mutableMapOf()
    private var recipePageCurrentOffset = 0

    /**
     * Default initialization
     * 20 recipes from database. Ids from 100 to 119
     * totalResults = 200 from sharedPref
     * favorites = id 110 true, id 112 = false
     */
    private data class RepositorySetup(
        val storedRecipes: List<StoredRecipe> = RecipeFactory.getListOfStoredRecipe(
            100,
            20
        ),
        val storedTotalResults: Int = 200,
        val storedFavorites: List<StoredFavoriteRecipe> = listOf(
            StoredFavoriteRecipe(110, true),
            StoredFavoriteRecipe(112, false)
        )
    )

    private fun setup(repositorySetup: RepositorySetup = RepositorySetup()) {
        // WHEN
        coEvery { recipesDao.get() } returns repositorySetup.storedRecipes
        coEvery { favoriteRecipeDao.get() } returns repositorySetup.storedFavorites
        every { recipePreferences.getRecipeListTotalResults() } returns repositorySetup.storedTotalResults

        favoriteRecipes = repositorySetup.storedFavorites.associateBy(
            { it.id }, { it.favorite }
        ).toMutableMap()
        recipePageCurrentOffset = repositorySetup.storedRecipes.size

        subject = RecipeRepositoryImpl(
            recipeService = recipeService,
            recipesDao = recipesDao,
            recipeDetailsDao = recipeDetailsDao,
            favoriteRecipeDao = favoriteRecipeDao,
            recipePreferences = recipePreferences,
            ioDispatcher = Dispatchers.Unconfined
        )
    }

    @Test
    fun `repository initialization without available local data`() = testScope.runTest {
        // GIVEN
        val storedRecipes = emptyList<StoredRecipe>()
        val storedTotalResults = 100
        val storedFavorites = emptyList<StoredFavoriteRecipe>()

        // WHEN
        coEvery { recipesDao.get() } returns storedRecipes
        coEvery { favoriteRecipeDao.get() } returns storedFavorites
        every { recipePreferences.getRecipeListTotalResults() } returns storedTotalResults

        val repository = RecipeRepositoryImpl(
            recipeService = recipeService,
            recipesDao = recipesDao,
            recipeDetailsDao = recipeDetailsDao,
            favoriteRecipeDao = favoriteRecipeDao,
            recipePreferences = recipePreferences,
            ioDispatcher = Dispatchers.Unconfined
        )

        // THEN
        coVerify { recipesDao.get() }
        coVerify { favoriteRecipeDao.get() }
        verify { recipePreferences.getRecipeListTotalResults() }

        val recipePageList: MutableList<Recipe?> = MutableList(storedTotalResults, init = { null })
        assertEquals(recipePageList, repository.recipePage.value.list)
    }

    @Test
    fun `empty repository initialization when failure`() =
        testScope.runTest {
            // WHEN
            coEvery { recipesDao.get() } throws RuntimeException(exceptionMessage)

            val repository = RecipeRepositoryImpl(
                recipeService = recipeService,
                recipesDao = recipesDao,
                recipeDetailsDao = recipeDetailsDao,
                favoriteRecipeDao = favoriteRecipeDao,
                recipePreferences = recipePreferences,
                ioDispatcher = Dispatchers.Unconfined
            )

            // THEN
            coVerify { recipesDao.get() }
            coVerify(exactly = 0) { favoriteRecipeDao.get() }
            verify(exactly = 0) { recipePreferences.getRecipeListTotalResults() }

            val recipePage = RecipePage(
                list = mutableListOf(),
                totalResults = 0
            )
            assertEquals(recipePage, repository.recipePage.value)
        }

    @Test
    fun `repository initialization succeeds when available local data`() = testScope.runTest {
        // GIVEN
        val totalStoredRecipes = 20
        val storedRecipes =
            RecipeFactory.getListOfStoredRecipe(firstRecipeId = 100, totalStoredRecipes)
        val storedTotalResults = 100
        val storedFavorites = emptyList<StoredFavoriteRecipe>()

        // WHEN
        coEvery { recipesDao.get() } returns storedRecipes
        coEvery { favoriteRecipeDao.get() } returns storedFavorites
        every { recipePreferences.getRecipeListTotalResults() } returns storedTotalResults

        val repository = RecipeRepositoryImpl(
            recipeService = recipeService,
            recipesDao = recipesDao,
            recipeDetailsDao = recipeDetailsDao,
            favoriteRecipeDao = favoriteRecipeDao,
            recipePreferences = recipePreferences,
            ioDispatcher = Dispatchers.Unconfined
        )

        // THEN
        coVerify { recipesDao.get() }
        coVerify { favoriteRecipeDao.get() }
        verify { recipePreferences.getRecipeListTotalResults() }

        val favorites = storedFavorites.associateBy({ it.id }, { it.favorite }).toMutableMap()
        val mappedRecipes = RecipeMapper.mapFromStoredRecipes(storedRecipes)
        val recipePageList: MutableList<Recipe?> = MutableList(storedTotalResults, init = { null })
        mappedRecipes.forEachIndexed { index, recipe ->
            recipePageList[index] = recipe.copy(
                isFavorite = favorites[recipe.id] ?: false
            )
        }
        assertEquals(recipePageList, repository.recipePage.value.list)
        assertEquals(repository.recipePage.value.list.filterNotNull().size, totalStoredRecipes)
    }

    @Test
    fun `updating favoriteRecipe, inserting new value in db and updating local recipePage`() =
        testScope.runTest {
            setup()

            // GIVEN
            val recipeId = 101L
            val isFavorite = true
            val storedFavorite = StoredFavoriteRecipe(recipeId, isFavorite)
            val recipePage = subject.recipePage.value.copy()

            // WHEN
            coEvery { favoriteRecipeDao.upsert(storedFavorite) } just Runs
            subject.setRecipeFavorite(id = recipeId, isFavorite = isFavorite)

            // THEN
            coVerify { favoriteRecipeDao.upsert(storedFavorite) }

            val recipeIndex = subject.recipePage.value.list.indexOfFirst {
                it?.id == recipeId
            }
            assertNotEquals(recipeIndex, -1)
            val recipe = subject.recipePage.value.list[recipeIndex]
            assertEquals(recipe?.isFavorite, isFavorite)
            assertNotEquals(subject.recipePage.value, recipePage)
        }

    @Test
    fun `updating favoriteRecipe, updating the value in db and in local recipePage`() =
        testScope.runTest {
            setup()

            // GIVEN
            val recipeId = 112L
            val isFavorite = true
            val storedFavorite = StoredFavoriteRecipe(recipeId, isFavorite)
            val recipePage = subject.recipePage.value.copy()

            // WHEN
            coEvery { favoriteRecipeDao.upsert(storedFavorite) } just Runs
            subject.setRecipeFavorite(id = recipeId, isFavorite = isFavorite)

            // THEN
            coVerify { favoriteRecipeDao.upsert(storedFavorite) }

            val recipeIndex = subject.recipePage.value.list.indexOfFirst {
                it?.id == recipeId
            }
            assertNotEquals(recipeIndex, -1)
            val recipe = subject.recipePage.value.list[recipeIndex]
            assertEquals(recipe?.isFavorite, isFavorite)
            assertNotEquals(subject.recipePage.value, recipePage)
        }

    @Test
    fun `updating favoriteRecipe without reference in the local recipePage, inserts the new value in db though`() =
        testScope.runTest {
            setup()

            // GIVEN
            val recipeId = 555L
            val isFavorite = true
            val storedFavorite = StoredFavoriteRecipe(recipeId, isFavorite)
            val recipePage = subject.recipePage.value.copy()

            // WHEN
            coEvery { favoriteRecipeDao.upsert(storedFavorite) } just Runs
            subject.setRecipeFavorite(id = recipeId, isFavorite = isFavorite)

            // THEN
            coVerify { favoriteRecipeDao.upsert(storedFavorite) }

            val recipeIndex = subject.recipePage.value.list.indexOfFirst {
                it?.id == recipeId
            }
            assertEquals(recipeIndex, -1)
            assertEquals(subject.recipePage.value, recipePage)
        }

    @Test
    fun `fetchRecipeList does not request data from remote when the currentIndex is smaller than the currentOffset`() =
        testScope.runTest {
            setup()

            // GIVEN
            val elementIndex = recipePageCurrentOffset - 1
            val recipePage = subject.recipePage.value.copy()

            // WHEN
            subject.fetchRecipeList(elementIndex, SortCriteria.RELEVANCE, SortOrder.DESCENDING)

            // THEN
            coVerify(exactly = 0) {
                recipeService.fetchRecipes(
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any()
                )
            }
            coVerify(exactly = 0) { recipePreferences.updateRecipeListTotalResults(any()) }
            coVerify(exactly = 0) { recipesDao.upsert(any()) }
            coVerify(exactly = 0) { recipesDao.deleteAll() }

            assertEquals(subject.recipePage.value, recipePage)
        }

    @Test
    fun `fetchRecipeList performs a data refreshing fetching the data related to page 1 when elementIndex = 0 and recipePageCurrentOffset = 0`() =
        testScope.runTest {

            setup(
                RepositorySetup(
                    storedRecipes = RecipeFactory.getListOfStoredRecipe(
                        100,
                        0
                    ),
                    storedTotalResults = 8,
                    storedFavorites = listOf(
                        StoredFavoriteRecipe(500, true),
                        StoredFavoriteRecipe(101, true)
                    )
                )
            )

            // GIVEN
            val elementIndex = recipePageCurrentOffset
            val recipePage = subject.recipePage.value.copy()
            val sortCriteria = SortCriteria.RELEVANCE
            val sortOrder = SortOrder.DESCENDING
            val recipesResponseAmount = 5
            val recipePageResponse = RecipeFactory.getRecipePageResponse(
                totalResults = 8,
                firstRecipeId = 100,
                recipesAmount = recipesResponseAmount
            )
            val recipesPage = RecipeMapper.mapFromRecipePageResponse(recipePageResponse)
            val storedRecipes = RecipeMapper.mapToStoredRecipes(recipesPage.list)

            // WHEN
            coEvery {
                recipeService.fetchRecipes(
                    offset = elementIndex,
                    limit = RecipeRepositoryImpl.itemsPerPageLimit,
                    query = null,
                    includeIngredients = null,
                    sortCriteria = RecipeMapper.mapSortCriteria(sortCriteria),
                    sortOrder = RecipeMapper.mapSortOrder(sortOrder)
                )
            } returns recipePageResponse
            coEvery { recipePreferences.updateRecipeListTotalResults(recipesPage.totalResults) } just Runs
            coEvery { recipesDao.upsert(storedRecipes) } just Runs
            coEvery { recipesDao.deleteAll() } just Runs

            subject.fetchRecipeList(elementIndex, SortCriteria.RELEVANCE, SortOrder.DESCENDING)

            // THEN
            coVerify {
                recipeService.fetchRecipes(
                    offset = elementIndex,
                    limit = RecipeRepositoryImpl.itemsPerPageLimit,
                    query = null,
                    includeIngredients = null,
                    sortCriteria = RecipeMapper.mapSortCriteria(sortCriteria),
                    sortOrder = RecipeMapper.mapSortOrder(sortOrder)
                )
            }
            coVerify { recipePreferences.updateRecipeListTotalResults(recipesPage.totalResults) }
            coVerify { recipesDao.upsert(storedRecipes) }
            coVerify { recipesDao.deleteAll() }

            // The resulting elements amount in the list equals the previous amount plus the size of the
            // response or the items per page limit (the smaller value of them)
            assertEquals(
                subject.recipePage.value.list.filterNotNull().count(),
                recipePageCurrentOffset + min(
                    recipesPage.list.size,
                    RecipeRepositoryImpl.itemsPerPageLimit
                )
            )
            recipesPage.list.forEachIndexed { index, recipe ->
                recipePage.list[elementIndex + index] =
                    recipe?.copy(isFavorite = favoriteRecipes[recipe.id] ?: false)
            }
            // on Refresh the list of the response must be equals to the non null elements of the list
            assertEquals(subject.recipePage.value, recipePage)
        }

    @Test
    fun `fetchRecipeList succeeds without updating local data when the recipe list in the remote response is empty`() =
        testScope.runTest {
            setup()

            // GIVEN
            val elementIndex = recipePageCurrentOffset + 1
            val previousRecipePage = subject.recipePage.value.copy()
            val sortCriteria = SortCriteria.RELEVANCE
            val sortOrder = SortOrder.DESCENDING
            val recipesResponseAmount = 0
            val recipePageResponse = RecipeFactory.getRecipePageResponse(
                totalResults = subject.recipePage.value.totalResults,
                firstRecipeId = 500,
                recipesAmount = recipesResponseAmount
            )

            // WHEN
            coEvery {
                recipeService.fetchRecipes(
                    offset = elementIndex,
                    limit = RecipeRepositoryImpl.itemsPerPageLimit,
                    query = null,
                    includeIngredients = null,
                    sortCriteria = RecipeMapper.mapSortCriteria(sortCriteria),
                    sortOrder = RecipeMapper.mapSortOrder(sortOrder)
                )
            } returns recipePageResponse

            subject.fetchRecipeList(elementIndex, SortCriteria.RELEVANCE, SortOrder.DESCENDING)

            // THEN
            coVerify {
                recipeService.fetchRecipes(
                    offset = elementIndex,
                    limit = RecipeRepositoryImpl.itemsPerPageLimit,
                    query = null,
                    includeIngredients = null,
                    sortCriteria = RecipeMapper.mapSortCriteria(sortCriteria),
                    sortOrder = RecipeMapper.mapSortOrder(sortOrder)
                )
            }
            coVerify(exactly = 0) { recipePreferences.updateRecipeListTotalResults(any()) }
            coVerify(exactly = 0) { recipesDao.upsert(any()) }
            coVerify(exactly = 0) { recipesDao.deleteAll() }

            // No changes on our recipe list
            assertEquals(
                subject.recipePage.value.list.filterNotNull().count(),
                recipePageCurrentOffset
            )
            assertEquals(subject.recipePage.value, previousRecipePage)
        }

    @Test
    fun `fetchRecipeList succeeds fetching the next page when elementIndex is not less than recipePageCurrentOffset and maxOffset still not reached`() =
        testScope.runTest {
            setup()

            // GIVEN
            val elementIndex = recipePageCurrentOffset + 1
            val previousRecipePage = subject.recipePage.value.copy()
            val sortCriteria = SortCriteria.RELEVANCE
            val sortOrder = SortOrder.DESCENDING
            val recipesResponseAmount = 5
            val recipePageResponse = RecipeFactory.getRecipePageResponse(
                totalResults = subject.recipePage.value.totalResults,
                firstRecipeId = 500,
                recipesAmount = recipesResponseAmount
            )
            val recipePage = RecipeMapper.mapFromRecipePageResponse(recipePageResponse)
            val storedRecipes = RecipeMapper.mapToStoredRecipes(recipePage.list)

            // WHEN
            coEvery {
                recipeService.fetchRecipes(
                    offset = elementIndex,
                    limit = RecipeRepositoryImpl.itemsPerPageLimit,
                    query = null,
                    includeIngredients = null,
                    sortCriteria = RecipeMapper.mapSortCriteria(sortCriteria),
                    sortOrder = RecipeMapper.mapSortOrder(sortOrder)
                )
            } returns recipePageResponse
            coEvery { recipePreferences.updateRecipeListTotalResults(recipePage.totalResults) } just Runs
            coEvery { recipesDao.upsert(storedRecipes) } just Runs

            subject.fetchRecipeList(elementIndex, sortCriteria, sortOrder)

            // THEN
            coVerify {
                recipeService.fetchRecipes(
                    offset = elementIndex,
                    limit = RecipeRepositoryImpl.itemsPerPageLimit,
                    query = null,
                    includeIngredients = null,
                    sortCriteria = RecipeMapper.mapSortCriteria(sortCriteria),
                    sortOrder = RecipeMapper.mapSortOrder(sortOrder)
                )
            }
            coVerify { recipePreferences.updateRecipeListTotalResults(recipePage.totalResults) }
            coVerify { recipesDao.upsert(storedRecipes) }
            coVerify(exactly = 0) { recipesDao.deleteAll() }

            // The resulting elements amount in the list equals the previous amount plus the size of the
            // response or the items per page limit (the smaller value of them)
            assertEquals(
                subject.recipePage.value.list.filterNotNull().count(),
                recipePageCurrentOffset + min(
                    recipePage.list.size,
                    RecipeRepositoryImpl.itemsPerPageLimit
                )
            )
            recipePage.list.forEachIndexed { index, recipe ->
                previousRecipePage.list[elementIndex + index] = recipe
            }
            assertEquals(subject.recipePage.value, previousRecipePage)
        }

    @Test
    fun `fetchRecipeList succeeds fetching from remote but only updates remainder recipes when offset reaches its max value `() =
        testScope.runTest {
            val recipesInDb = 10
            val totalRecipesAmount = 15
            val remainingSpace = totalRecipesAmount - recipesInDb

            setup(
                RepositorySetup(
                    storedRecipes = RecipeFactory.getListOfStoredRecipe(
                        100,
                        recipesInDb
                    ),
                    storedTotalResults = totalRecipesAmount,
                    storedFavorites = emptyList()
                )
            )

            // GIVEN
            val elementIndex = recipesInDb
            val recipePage = subject.recipePage.value.copy()
            val sortCriteria = SortCriteria.RELEVANCE
            val sortOrder = SortOrder.DESCENDING
            val recipePageResponse = RecipeFactory.getRecipePageResponse(
                totalResults = totalRecipesAmount,
                firstRecipeId = 500,
                recipesAmount = 33
            )
            // The recipes being taken are as much as the available space
            val recipes =
                RecipeMapper.mapFromRecipePageResponse(recipePageResponse).list
                    .take(remainingSpace)
                    .map {
                        it?.copy(isFavorite = favoriteRecipes[it.id] ?: false)
                    }
            val storedRecipes = RecipeMapper.mapToStoredRecipes(recipes)

            // WHEN
            coEvery {
                recipeService.fetchRecipes(
                    offset = elementIndex,
                    limit = RecipeRepositoryImpl.itemsPerPageLimit,
                    query = null,
                    includeIngredients = null,
                    sortCriteria = RecipeMapper.mapSortCriteria(sortCriteria),
                    sortOrder = RecipeMapper.mapSortOrder(sortOrder)
                )
            } returns recipePageResponse
            coEvery { recipePreferences.updateRecipeListTotalResults(totalRecipesAmount) } just Runs
            coEvery { recipesDao.upsert(storedRecipes) } just Runs

            subject.fetchRecipeList(elementIndex, sortCriteria, sortOrder)

            // THEN
            coVerify {
                recipeService.fetchRecipes(
                    offset = elementIndex,
                    limit = RecipeRepositoryImpl.itemsPerPageLimit,
                    query = null,
                    includeIngredients = null,
                    sortCriteria = RecipeMapper.mapSortCriteria(sortCriteria),
                    sortOrder = RecipeMapper.mapSortOrder(sortOrder)
                )
            }
            coVerify { recipePreferences.updateRecipeListTotalResults(totalRecipesAmount) }
            coVerify { recipesDao.upsert(storedRecipes) }
            coVerify(exactly = 0) { recipesDao.deleteAll() }

            // The amount is equals to the total amount defined in the response
            assertEquals(
                subject.recipePage.value.list.filterNotNull().count(),
                totalRecipesAmount
            )
            recipes.forEachIndexed { index, recipe ->
                recipePage.list[elementIndex + index] = recipe
            }
            // The recipes list matches the previous one plus the new elements
            assertEquals(subject.recipePage.value, recipePage)
        }

    @Test
    fun `refreshRecipeList performs a refresh when elementIndex = 0 and recipePageCurrentOffset = 0`() =
        testScope.runTest {
            val totalResults = 40
            setup(
                RepositorySetup(
                    storedRecipes = RecipeFactory.getListOfStoredRecipe(
                        100,
                        10
                    ),
                    storedTotalResults = totalResults,
                    storedFavorites = listOf(
                        StoredFavoriteRecipe(500, true),
                        StoredFavoriteRecipe(101, true)
                    )
                )
            )

            // GIVEN
            val elementIndex = 0
            val sortCriteria = SortCriteria.RELEVANCE
            val sortOrder = SortOrder.DESCENDING
            val recipesResponseAmount = 5
            val recipePageResponse = RecipeFactory.getRecipePageResponse(
                totalResults = totalResults,
                firstRecipeId = 100,
                recipesAmount = recipesResponseAmount
            )
            val recipesPage = RecipeMapper.mapFromRecipePageResponse(recipePageResponse)
            val recipes: List<Recipe?> = recipesPage.list.map {
                it?.copy(isFavorite = favoriteRecipes[it.id] ?: false)
            }
            val storedRecipes = RecipeMapper.mapToStoredRecipes(recipes)
            val updatedRecipes: MutableList<Recipe?> =
                MutableList<Recipe?>(recipesPage.totalResults, init = { null })
            recipes.forEachIndexed { index, recipe ->
                updatedRecipes[elementIndex + index] = recipe
            }

            // WHEN
            coEvery {
                recipeService.fetchRecipes(
                    offset = elementIndex,
                    limit = RecipeRepositoryImpl.itemsPerPageLimit,
                    query = null,
                    includeIngredients = null,
                    sortCriteria = RecipeMapper.mapSortCriteria(sortCriteria),
                    sortOrder = RecipeMapper.mapSortOrder(sortOrder)
                )
            } returns recipePageResponse
            coEvery { recipePreferences.updateRecipeListTotalResults(recipesPage.totalResults) } just Runs
            coEvery { recipesDao.upsert(storedRecipes) } returns Unit
            coEvery { recipesDao.deleteAll() } just Runs

            subject.refreshRecipeList(SortCriteria.RELEVANCE, SortOrder.DESCENDING)

            // THEN
            coVerify {
                recipeService.fetchRecipes(
                    offset = elementIndex,
                    limit = RecipeRepositoryImpl.itemsPerPageLimit,
                    query = null,
                    includeIngredients = null,
                    sortCriteria = RecipeMapper.mapSortCriteria(sortCriteria),
                    sortOrder = RecipeMapper.mapSortOrder(sortOrder)
                )
            }
            coVerify { recipePreferences.updateRecipeListTotalResults(recipesPage.totalResults) }
            coVerify { recipesDao.upsert(storedRecipes) }
            coVerify { recipesDao.deleteAll() }

            // on Refresh the size of the non null elements of the list must be equals to the response size
            assertEquals(
                recipePageResponse.results.size,
                subject.recipePage.value.list.filterNotNull().size
            )
            // on Refresh the list of the response must be equals to the non null elements of the list
            assertEquals(recipes, subject.recipePage.value.list.filterNotNull())
        }

    @Test
    fun `searchRecipeList succeeds searching recipes on remote when searching by name`() =
        testScope.runTest {
            setup()

            // GIVEN
            val query = "Pasta"
            val sortCriteria = SortCriteria.RELEVANCE
            val sortOrder = SortOrder.DESCENDING
            val recipesResponseAmount = 5
            val recipePageResponse = RecipeFactory.getRecipePageResponse(
                totalResults = subject.recipePage.value.totalResults,
                firstRecipeId = 500,
                recipesAmount = recipesResponseAmount
            )
            val recipePage = RecipeMapper.mapFromRecipePageResponse(recipePageResponse)

            // WHEN
            coEvery {
                recipeService.fetchRecipes(
                    offset = 0,
                    limit = 20,
                    query = query,
                    includeIngredients = null,
                    sortCriteria = RecipeMapper.mapSortCriteria(sortCriteria),
                    sortOrder = RecipeMapper.mapSortOrder(sortOrder)
                )
            } returns recipePageResponse

            val result = subject.searchRecipeList(
                query,
                SearchCriteria.NAME,
                sortCriteria,
                sortOrder
            )

            // THEN
            coVerify {
                recipeService.fetchRecipes(
                    offset = 0,
                    limit = 20,
                    query = query,
                    includeIngredients = null,
                    sortCriteria = RecipeMapper.mapSortCriteria(sortCriteria),
                    sortOrder = RecipeMapper.mapSortOrder(sortOrder)
                )
            }
            coVerify(exactly = 0) { recipePreferences.updateRecipeListTotalResults(any()) }
            coVerify(exactly = 0) { recipesDao.upsert(any()) }
            coVerify(exactly = 0) { recipesDao.deleteAll() }

            assertEquals(result, recipePage.list)
        }

    @Test
    fun `searchRecipeList succeeds searching recipes on remote when searching by ingredients`() =
        testScope.runTest {
            setup()

            // GIVEN
            val query = "Pasta Tomato".split("[\\s,]+".toRegex()).joinToString(",")
            val sortCriteria = SortCriteria.RELEVANCE
            val sortOrder = SortOrder.DESCENDING
            val recipesResponseAmount = 5
            val recipePageResponse = RecipeFactory.getRecipePageResponse(
                totalResults = subject.recipePage.value.totalResults,
                firstRecipeId = 500,
                recipesAmount = recipesResponseAmount
            )
            val recipePage = RecipeMapper.mapFromRecipePageResponse(recipePageResponse)

            // WHEN
            coEvery {
                recipeService.fetchRecipes(
                    offset = 0,
                    limit = 20,
                    query = null,
                    includeIngredients = query,
                    sortCriteria = RecipeMapper.mapSortCriteria(sortCriteria),
                    sortOrder = RecipeMapper.mapSortOrder(sortOrder)
                )
            } returns recipePageResponse

            val result = subject.searchRecipeList(
                query,
                SearchCriteria.INGREDIENTS,
                sortCriteria,
                sortOrder
            )

            // THEN
            coVerify {
                recipeService.fetchRecipes(
                    offset = 0,
                    limit = 20,
                    query = null,
                    includeIngredients = query,
                    sortCriteria = RecipeMapper.mapSortCriteria(sortCriteria),
                    sortOrder = RecipeMapper.mapSortOrder(sortOrder)
                )
            }
            coVerify(exactly = 0) { recipePreferences.updateRecipeListTotalResults(any()) }
            coVerify(exactly = 0) { recipesDao.upsert(any()) }
            coVerify(exactly = 0) { recipesDao.deleteAll() }

            assertEquals(result, recipePage.list)
        }

    @Test
    fun `getRecipeDetailsById succeeds getting the recipeDetails from database`() =
        testScope.runTest {
            setup()

            // GIVEN
            val recipeId = 9999L
            val storedRecipeDetails = RecipeDetailsFactory.getStoredRecipeDetails(recipeId)
            val recipeDetails = RecipeDetailsMapper.mapFromStoredRecipeDetails(storedRecipeDetails)

            // WHEN
            coEvery {
                recipeDetailsDao.getRecipeById(recipeId)
            } returns storedRecipeDetails

            val result = subject.getRecipeDetailsById(recipeId)

            // THEN
            coVerify { recipeDetailsDao.getRecipeById(recipeId) }

            assertEquals(
                recipeDetails,
                result
            )
        }

    @Test
    fun `getRecipeDetailsById succeeds getting the favorite recipeDetails from database`() =
        testScope.runTest {
            setup()

            // GIVEN
            val recipeId = 110L
            val storedRecipeDetails = RecipeDetailsFactory.getStoredRecipeDetails(recipeId)
            val recipeDetails = RecipeDetailsMapper.mapFromStoredRecipeDetails(storedRecipeDetails)
                .copy(isFavorite = true)

            // WHEN
            coEvery { recipeDetailsDao.getRecipeById(recipeId) } returns storedRecipeDetails

            val result = subject.getRecipeDetailsById(recipeId)

            // THEN
            coVerify { recipeDetailsDao.getRecipeById(recipeId) }

            assertEquals(
                recipeDetails,
                result
            )
        }

    @Test
    fun `getRecipeDetailsById does not find favorite recipeDetails at database, so it fetches it from remote and inserts it in database`() =
        testScope.runTest {
            setup()

            // GIVEN
            val recipeId = 110L
            val recipeDetailsResponse = RecipeDetailsFactory.getRecipeDetailsResponse(recipeId)
            val recipeDetails =
                RecipeDetailsMapper.mapFromRecipeDetailsResponse(recipeDetailsResponse)
                    .copy(isFavorite = true)
            val storedRecipeDetails = RecipeDetailsMapper.mapToStoredRecipeDetails(recipeDetails)

            // WHEN
            coEvery { recipeDetailsDao.getRecipeById(recipeId) } returns null
            coEvery { recipeService.fetchRecipeDetails(recipeId) } returns recipeDetailsResponse
            coEvery { recipeDetailsDao.upsert(storedRecipeDetails) } just Runs

            val result = subject.getRecipeDetailsById(recipeId)

            // THEN
            coVerify { recipeDetailsDao.getRecipeById(recipeId) }
            coVerify { recipeService.fetchRecipeDetails(recipeId) }
            coVerify { recipeDetailsDao.upsert(storedRecipeDetails) }

            assertEquals(
                recipeDetails,
                result
            )
        }

    @Test
    fun `fetchRecipeDetails fetches from remote and inserts the favorite recipeDetails in database`() =
        testScope.runTest {
            setup()

            // GIVEN
            val recipeId = 110L
            val recipeDetailsResponse = RecipeDetailsFactory.getRecipeDetailsResponse(recipeId)
            val recipeDetails =
                RecipeDetailsMapper.mapFromRecipeDetailsResponse(recipeDetailsResponse)
                    .copy(isFavorite = true)
            val storedRecipeDetails = RecipeDetailsMapper.mapToStoredRecipeDetails(recipeDetails)

            // WHEN
            coEvery { recipeService.fetchRecipeDetails(recipeId) } returns recipeDetailsResponse
            coEvery { recipeDetailsDao.upsert(storedRecipeDetails) } just Runs

            val result = subject.fetchRecipeDetails(recipeId)

            // THEN
            coVerify { recipeService.fetchRecipeDetails(recipeId) }
            coVerify { recipeDetailsDao.upsert(storedRecipeDetails) }

            assertEquals(
                recipeDetails,
                result
            )
        }

    @Test
    fun `fetchRecipeDetails fetches from remote and inserts the recipeDetails in database`() =
        testScope.runTest {
            setup()

            // GIVEN
            val recipeId = 3333L
            val recipeDetailsResponse = RecipeDetailsFactory.getRecipeDetailsResponse(recipeId)
            val recipeDetails =
                RecipeDetailsMapper.mapFromRecipeDetailsResponse(recipeDetailsResponse)
                    .copy(isFavorite = false)
            val storedRecipeDetails = RecipeDetailsMapper.mapToStoredRecipeDetails(recipeDetails)

            // WHEN
            coEvery { recipeService.fetchRecipeDetails(recipeId) } returns recipeDetailsResponse
            coEvery { recipeDetailsDao.upsert(storedRecipeDetails) } just Runs

            val result = subject.fetchRecipeDetails(recipeId)

            // THEN
            coVerify { recipeService.fetchRecipeDetails(recipeId) }
            coVerify { recipeDetailsDao.upsert(storedRecipeDetails) }

            assertEquals(
                recipeDetails,
                result
            )
        }
}
