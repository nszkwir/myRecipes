package com.spitzer.data.mapper

import com.spitzer.data.factory.RecipeDetailsFactory.getRecipeDetailsResponse
import com.spitzer.data.repository.mapper.RecipeDetailsMapper
import junit.framework.TestCase.assertEquals
import org.junit.Test

class RecipeDetailsMapperTest {
    @Test
    fun `mapping from Remote to Entity, and then to Stored provides same models when reverse mapping`() {
        // GIVEN, WHEN
        val recipeDetails =
            RecipeDetailsMapper.mapFromRecipeDetailsResponse(getRecipeDetailsResponse())
        val storedEntity = RecipeDetailsMapper.mapToStoredRecipeDetails(recipeDetails)
        val reversedRecipeDetails = RecipeDetailsMapper.mapFromStoredRecipeDetails(storedEntity)

        // THEN
        assertEquals(recipeDetails, reversedRecipeDetails)
    }

    @Test
    fun `mapping from Remote (with null url) to Entity, and then to Stored provides same models when reverse mapping`() {
        // GIVEN, WHEN
        val recipeDetails = RecipeDetailsMapper.mapFromRecipeDetailsResponse(
            getRecipeDetailsResponse(
                imageUrl = null,
                sourceUrl = null
            )
        )
        val storedEntity = RecipeDetailsMapper.mapToStoredRecipeDetails(recipeDetails)
        val reversedRecipeDetails = RecipeDetailsMapper.mapFromStoredRecipeDetails(storedEntity)

        // THEN
        assertEquals(recipeDetails, reversedRecipeDetails)
    }
}
