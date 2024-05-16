package com.spitzer.data.storage.database.room.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.spitzer.data.storage.database.room.dto.StoredRecipeDetails

@Dao
interface RecipeDetailsDao {

    @Query("SELECT * FROM recipe_details WHERE id = :id LIMIT 1")
    suspend fun getRecipeById(id: Long): StoredRecipeDetails?

    @Transaction
    @Upsert
    suspend fun upsert(recipe: StoredRecipeDetails)
}
