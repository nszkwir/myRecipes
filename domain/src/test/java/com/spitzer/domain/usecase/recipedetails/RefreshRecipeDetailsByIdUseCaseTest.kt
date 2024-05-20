package com.spitzer.domain.usecase.recipedetails

import com.spitzer.contracts.RecipeRepository
import com.spitzer.domain.factory.RecipeDetailsFactory
import com.spitzer.domain.utils.WrappedResult
import com.spitzer.entity.network.NetworkError
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class RefreshRecipeDetailsByIdUseCaseTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    private val testScope = TestScope(UnconfinedTestDispatcher())

    private lateinit var subject: RefreshRecipeDetailsByIdUseCase

    private val recipeRepository = mockk<RecipeRepository>(relaxed = true)

    @Before
    fun setup() {
        subject = RefreshRecipeDetailsByIdUseCase(recipeRepository)
    }

    @Test
    fun `RefreshRecipeDetailsByIdUseCase succeeds requesting fetchRecipeDetails`() =
        testScope.runTest {
            // GIVEN
            val id = 100L
            val recipeDetails = RecipeDetailsFactory.getRecipeDetails(id)

            // WHEN
            coEvery { recipeRepository.fetchRecipeDetails(id) } returns recipeDetails

            val result = subject.invoke(id)
            val data = (result as WrappedResult.Success).data

            // THEN
            coVerify { recipeRepository.fetchRecipeDetails(id) }
            Assert.assertTrue(result is WrappedResult.Success)
            assertEquals(recipeDetails, data)
        }

    @Test
    fun `RefreshRecipeDetailsByIdUseCase handles the result when there is No Internet`() =
        testScope.runTest {
            // GIVEN
            val id = 100L

            // WHEN
            coEvery { recipeRepository.fetchRecipeDetails(id) } throws NetworkError.NoInternet

            val result = subject.invoke(id)
            val exception = (result as WrappedResult.Error).exception

            // THEN
            coVerify { recipeRepository.fetchRecipeDetails(id) }

            Assert.assertTrue(result is WrappedResult.Error)
            Assert.assertTrue(exception is RefreshRecipeDetailsByIdUseCaseError.NoInternet)
        }

    @Test
    fun `RefreshRecipeDetailsByIdUseCase handles the result when an Unknown Error occurs`() =
        testScope.runTest {
            // GIVEN
            val id = 100L

            // WHEN
            coEvery { recipeRepository.fetchRecipeDetails(id) } throws NetworkError.Unknown

            val result = subject.invoke(id)
            val exception = (result as WrappedResult.Error).exception

            // THEN
            coVerify { recipeRepository.fetchRecipeDetails(id) }

            Assert.assertTrue(result is WrappedResult.Error)
            Assert.assertTrue(exception is RefreshRecipeDetailsByIdUseCaseError.Generic)
        }

    @Test
    fun `RefreshRecipeDetailsByIdUseCase handles the result when an Exception occurs`() =
        testScope.runTest {
            // GIVEN
            val id = 100L

            // WHEN
            coEvery { recipeRepository.fetchRecipeDetails(id) } throws Throwable()

            val result = subject.invoke(id)
            val exception = (result as WrappedResult.Error).exception

            // THEN
            coVerify { recipeRepository.fetchRecipeDetails(id) }

            Assert.assertTrue(result is WrappedResult.Error)
            Assert.assertTrue(exception is RefreshRecipeDetailsByIdUseCaseError.Generic)
        }
}
