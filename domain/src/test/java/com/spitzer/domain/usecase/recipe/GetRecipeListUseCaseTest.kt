package com.spitzer.domain.usecase.recipe

import com.spitzer.contracts.RecipeRepository
import com.spitzer.entity.recipe.RecipePage
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class GetRecipeListUseCaseTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    private val testScope = TestScope(UnconfinedTestDispatcher())

    private lateinit var subject: GetRecipeListUseCase

    private val recipeRepository = mockk<RecipeRepository>(relaxed = true)

    @Before
    fun setup() {
        subject = GetRecipeListUseCase(recipeRepository)
    }

    @Test
    fun `GetRecipeListUseCase repository recipePage state`() = testScope.runTest {
        // GIVEN
        val recipePage: MutableStateFlow<RecipePage> = MutableStateFlow(
            RecipePage(mutableListOf(), 0)
        )

        // WHEN
        coEvery { recipeRepository.recipePage } returns recipePage
        val result = subject.invoke()

        // THEN
        coVerify { recipeRepository.recipePage }
        assertEquals(result, recipePage)
    }

}
