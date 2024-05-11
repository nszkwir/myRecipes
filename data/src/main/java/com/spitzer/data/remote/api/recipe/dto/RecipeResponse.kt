package com.spitzer.data.remote.api.recipe.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RecipeResponse(
    val id: Long,
    val title: String,
    val image: String,
    val summary: String
)
