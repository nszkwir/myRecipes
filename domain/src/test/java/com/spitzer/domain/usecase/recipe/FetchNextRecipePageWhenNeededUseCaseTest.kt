package com.spitzer.domain.usecase.recipe

import com.spitzer.contracts.RecipeRepository
import com.spitzer.domain.utils.WrappedResult
import com.spitzer.entity.network.NetworkError
import com.spitzer.entity.search.SortCriteria
import com.spitzer.entity.search.SortOrder
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class FetchNextRecipePageWhenNeededUseCaseTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    private val testScope = TestScope(UnconfinedTestDispatcher())

    private lateinit var subject: FetchNextRecipePageWhenNeededUseCase

    private val recipeRepository = mockk<RecipeRepository>(relaxed = true)

    @Before
    fun setup() {
        subject = FetchNextRecipePageWhenNeededUseCase(recipeRepository)
    }

    @Test
    fun `FetchNextRecipePageWhenNeededUseCase succeeds requesting the next page`() =
        testScope.runTest {
            // GIVEN
            val sortCriteria = SortCriteria.RELEVANCE
            val sortOrder = SortOrder.DESCENDING
            val elementIndex = 0

            // WHEN
            coEvery {
                recipeRepository.fetchRecipeList(
                    elementIndex = elementIndex,
                    sortCriteria = sortCriteria,
                    sortOrder = sortOrder
                )
            } just Runs

            val result = subject.invoke(elementIndex, sortCriteria, sortOrder)

            // THEN
            coVerify {
                recipeRepository.fetchRecipeList(
                    elementIndex = elementIndex,
                    sortCriteria = sortCriteria,
                    sortOrder = sortOrder
                )
            }
            Assert.assertTrue(result is WrappedResult.Success)
        }

    @Test
    fun `FetchNextRecipePageWhenNeededUseCase handles the result when there is No Internet`() =
        testScope.runTest {
            // GIVEN
            val sortCriteria = SortCriteria.RELEVANCE
            val sortOrder = SortOrder.DESCENDING
            val elementIndex = 0

            // WHEN
            coEvery {
                recipeRepository.fetchRecipeList(
                    elementIndex = elementIndex,
                    sortCriteria = sortCriteria,
                    sortOrder = sortOrder
                )
            } throws NetworkError.NoInternet

            val result = subject.invoke(elementIndex, sortCriteria, sortOrder)
            val exception = (result as WrappedResult.Error).exception

            // THEN
            coVerify {
                recipeRepository.fetchRecipeList(
                    elementIndex = elementIndex,
                    sortCriteria = sortCriteria,
                    sortOrder = sortOrder
                )
            }

            Assert.assertTrue(result is WrappedResult.Error)
            Assert.assertTrue(exception is FetchNextRecipePageWhenNeededError.Retry)
        }

    @Test
    fun `FetchNextRecipePageWhenNeededUseCase handles the result when an Unknown Error occurs`() =
        testScope.runTest {
            // GIVEN
            val sortCriteria = SortCriteria.RELEVANCE
            val sortOrder = SortOrder.DESCENDING
            val elementIndex = 0

            // WHEN
            coEvery {
                recipeRepository.fetchRecipeList(
                    elementIndex = elementIndex,
                    sortCriteria = sortCriteria,
                    sortOrder = sortOrder
                )
            } throws NetworkError.Unknown

            val result = subject.invoke(elementIndex, sortCriteria, sortOrder)
            val exception = (result as WrappedResult.Error).exception

            // THEN
            coVerify {
                recipeRepository.fetchRecipeList(
                    elementIndex = elementIndex,
                    sortCriteria = sortCriteria,
                    sortOrder = sortOrder
                )
            }

            Assert.assertTrue(result is WrappedResult.Error)
            Assert.assertTrue(exception is FetchNextRecipePageWhenNeededError.Retry)
        }

    @Test
    fun `FetchNextRecipePageWhenNeededUseCase handles the result when an Exception occurs`() =
        testScope.runTest {
            // GIVEN
            val sortCriteria = SortCriteria.RELEVANCE
            val sortOrder = SortOrder.DESCENDING
            val elementIndex = 0

            // WHEN
            coEvery {
                recipeRepository.fetchRecipeList(
                    elementIndex = elementIndex,
                    sortCriteria = sortCriteria,
                    sortOrder = sortOrder
                )
            } throws Exception()

            val result = subject.invoke(elementIndex, sortCriteria, sortOrder)
            val exception = (result as WrappedResult.Error).exception

            // THEN
            coVerify {
                recipeRepository.fetchRecipeList(
                    elementIndex = elementIndex,
                    sortCriteria = sortCriteria,
                    sortOrder = sortOrder
                )
            }

            Assert.assertTrue(result is WrappedResult.Error)
            Assert.assertTrue(exception is FetchNextRecipePageWhenNeededError.Retry)
        }
}
