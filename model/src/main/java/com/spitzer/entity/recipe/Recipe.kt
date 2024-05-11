package com.spitzer.entity.recipe

import java.net.URL

data class Recipe(
    val id: Long,
    val title: String,
    val image: URL?,
    val summary: String,
    val isFavorite: Boolean
)