package com.spitzer.data.storage.database.room.dto

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "favorite_recipe",
)
data class StoredFavoriteRecipe(
    @PrimaryKey
    val id: Long,
    val favorite: Boolean
)
