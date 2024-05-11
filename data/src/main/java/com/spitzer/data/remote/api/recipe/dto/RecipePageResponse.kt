package com.spitzer.data.remote.api.recipe.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RecipePageResponse(
    val results: List<RecipeResponse>,
    val totalResults: Int
)
