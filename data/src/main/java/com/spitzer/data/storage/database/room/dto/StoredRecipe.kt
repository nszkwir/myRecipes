package com.spitzer.data.storage.database.room.dto

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "recipe",
)
data class StoredRecipe(
    @PrimaryKey(autoGenerate = true)
    val index: Long? = null,
    val id: Long,
    val title: String,
    val image: String?,
    val summary: String
)
