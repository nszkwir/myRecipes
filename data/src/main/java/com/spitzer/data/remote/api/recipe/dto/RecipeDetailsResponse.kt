package com.spitzer.data.remote.api.recipe.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RecipeDetailsResponse(
    val id: Long,
    val title: String,
    val readyInMinutes: Int,
    val servings: Int,
    val summary: String,
    val instructions: String,
    val vegetarian: Boolean,
    val vegan: Boolean,
    val glutenFree: Boolean,
    val dairyFree: Boolean,
    val image: String?,
    val healthScore: Int?,
    val diets: List<String>?,
    val spoonacularScore: Double,
    val spoonacularSourceUrl: String?,
    val extendedIngredients: List<Ingredient>?
) {
    @JsonClass(generateAdapter = true)
    data class Ingredient(
        val original: String?,
        val image: String?
    )
}
