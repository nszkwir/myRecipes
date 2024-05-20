package com.spitzer.data.mapper

import com.spitzer.data.factory.RecipeFactory.getRecipePageResponse
import com.spitzer.data.repository.mapper.RecipeMapper
import junit.framework.TestCase.assertEquals
import org.junit.Test

class RecipeMapperTest {
    @Test
    fun `mapping from Remote to Entity, and then to Stored provides same models when reverse mapping`() {
        // GIVEN, WHEN
        val recipePage =
            RecipeMapper.mapFromRecipePageResponse(getRecipePageResponse())
        val storedEntities = RecipeMapper.mapToStoredRecipes(recipePage.list)
        val reversedRecipes = RecipeMapper.mapFromStoredRecipes(storedEntities)

        // THEN
        assertEquals(recipePage.list, reversedRecipes)
    }
}
