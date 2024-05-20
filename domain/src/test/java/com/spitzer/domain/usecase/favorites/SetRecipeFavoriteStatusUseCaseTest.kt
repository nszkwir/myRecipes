package com.spitzer.domain.usecase.favorites

import com.spitzer.contracts.RecipeRepository
import com.spitzer.domain.utils.WrappedResult
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

class SetRecipeFavoriteStatusUseCaseTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    private val testScope = TestScope(UnconfinedTestDispatcher())

    private lateinit var subject: SetRecipeFavoriteStatusUseCase

    private val recipeRepository = mockk<RecipeRepository>(relaxed = true)

    @Before
    fun setup() {
        subject = SetRecipeFavoriteStatusUseCase(recipeRepository)
    }

    @Test
    fun `SetRecipeFavoriteStatusUseCase succeeds updating the favorite status of the recipe`() =
        testScope.runTest {
            // GIVEN
            val id = 100L
            val isFavorite = false

            // WHEN
            coEvery { recipeRepository.setRecipeFavorite(id, isFavorite) } just Runs

            val result = subject.invoke(id, isFavorite)

            // THEN
            coVerify { recipeRepository.setRecipeFavorite(id, isFavorite) }
            Assert.assertTrue(result is WrappedResult.Success)
        }

    @Test
    fun `SetRecipeFavoriteStatusUseCase handles the result when an Exception occurs`() =
        testScope.runTest {
            // GIVEN
            val id = 100L
            val isFavorite = false

            // WHEN
            coEvery { recipeRepository.setRecipeFavorite(id, isFavorite) } throws Throwable()

            val result = subject.invoke(id, isFavorite)
            val exception = (result as WrappedResult.Error).exception

            // THEN
            coVerify { recipeRepository.setRecipeFavorite(id, isFavorite) }
            Assert.assertTrue(result is WrappedResult.Error)
            Assert.assertTrue(exception is SetRecipeFavoriteStatusUseCaseError.Generic)
        }
}
