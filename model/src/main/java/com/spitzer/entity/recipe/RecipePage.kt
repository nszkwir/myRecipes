package com.spitzer.entity.recipe

data class RecipePage(
    val list: MutableList<Recipe?>,
    val totalResults: Int
)