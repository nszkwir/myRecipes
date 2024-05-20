package com.spitzer.domain.usecase.recipe

import com.spitzer.contracts.RecipeRepository
import com.spitzer.domain.factory.RecipeFactory
import com.spitzer.domain.utils.WrappedResult
import com.spitzer.entity.network.NetworkError
import com.spitzer.entity.search.SearchCriteria
import com.spitzer.entity.search.SortCriteria
import com.spitzer.entity.search.SortOrder
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class SearchRecipePageUseCaseTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    private val testScope = TestScope(UnconfinedTestDispatcher())

    private lateinit var subject: SearchRecipePageUseCase

    private val recipeRepository = mockk<RecipeRepository>(relaxed = true)

    @Before
    fun setup() {
        subject = SearchRecipePageUseCase(recipeRepository)
    }

    @Test
    fun `SearchRecipePageUseCase succeeds searching the recipes`() = testScope.runTest {
        // GIVEN
        val query = "query"
        val searchCriteria = SearchCriteria.INGREDIENTS
        val sortCriteria = SortCriteria.RELEVANCE
        val sortOrder = SortOrder.DESCENDING
        val recipeList = RecipeFactory.getRecipeList(10, 100)

        // WHEN
        coEvery {
            recipeRepository.searchRecipeList(
                query,
                searchCriteria,
                sortCriteria,
                sortOrder
            )
        } returns recipeList

        val result = subject.invoke(query, searchCriteria, sortCriteria, sortOrder)
        val recipes = (result as WrappedResult.Success).data

        // THEN
        coVerify {
            recipeRepository.searchRecipeList(
                query,
                searchCriteria,
                sortCriteria,
                sortOrder
            )
        }
        Assert.assertTrue(result is WrappedResult.Success)
        assertEquals(recipeList, recipes)
    }

    @Test
    fun `SearchRecipePageUseCase handles the result when there is No Internet`() =
        testScope.runTest {
            // GIVEN
            val query = "query"
            val searchCriteria = SearchCriteria.INGREDIENTS
            val sortCriteria = SortCriteria.RELEVANCE
            val sortOrder = SortOrder.DESCENDING

            // WHEN
            coEvery {
                recipeRepository.searchRecipeList(
                    query,
                    searchCriteria,
                    sortCriteria,
                    sortOrder
                )
            } throws NetworkError.NoInternet

            val result = subject.invoke(query, searchCriteria, sortCriteria, sortOrder)
            val exception = (result as WrappedResult.Error).exception

            // THEN
            coVerify {
                recipeRepository.searchRecipeList(
                    query,
                    searchCriteria,
                    sortCriteria,
                    sortOrder
                )
            }
            Assert.assertTrue(result is WrappedResult.Error)
            Assert.assertTrue(exception is SearchRecipePageUseCaseError.NoInternet)
        }

    @Test
    fun `SearchRecipePageUseCase handles the result when an Unknown Error occurs`() =
        testScope.runTest {
            // GIVEN
            val query = "query"
            val searchCriteria = SearchCriteria.INGREDIENTS
            val sortCriteria = SortCriteria.RELEVANCE
            val sortOrder = SortOrder.DESCENDING

            // WHEN
            coEvery {
                recipeRepository.searchRecipeList(
                    query,
                    searchCriteria,
                    sortCriteria,
                    sortOrder
                )
            } throws NetworkError.Unknown

            val result = subject.invoke(query, searchCriteria, sortCriteria, sortOrder)
            val exception = (result as WrappedResult.Error).exception

            // THEN
            coVerify {
                recipeRepository.searchRecipeList(
                    query,
                    searchCriteria,
                    sortCriteria,
                    sortOrder
                )
            }
            Assert.assertTrue(result is WrappedResult.Error)
            Assert.assertTrue(exception is SearchRecipePageUseCaseError.Generic)
        }

    @Test
    fun `SearchRecipePageUseCase handles the result when an Exception occurs`() =
        testScope.runTest {
            // GIVEN
            val query = "query"
            val searchCriteria = SearchCriteria.INGREDIENTS
            val sortCriteria = SortCriteria.RELEVANCE
            val sortOrder = SortOrder.DESCENDING

            // WHEN
            coEvery {
                recipeRepository.searchRecipeList(
                    query,
                    searchCriteria,
                    sortCriteria,
                    sortOrder
                )
            } throws Exception()

            val result = subject.invoke(query, searchCriteria, sortCriteria, sortOrder)
            val exception = (result as WrappedResult.Error).exception

            // THEN
            coVerify {
                recipeRepository.searchRecipeList(
                    query,
                    searchCriteria,
                    sortCriteria,
                    sortOrder
                )
            }
            Assert.assertTrue(result is WrappedResult.Error)
            Assert.assertTrue(exception is SearchRecipePageUseCaseError.Generic)
        }
}
