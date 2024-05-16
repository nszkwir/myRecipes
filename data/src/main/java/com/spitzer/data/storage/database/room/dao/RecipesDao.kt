package com.spitzer.data.storage.database.room.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.spitzer.data.storage.database.room.dto.StoredRecipe

@Dao
interface RecipesDao {

    @Query("SELECT * FROM recipe")
    fun get(): List<StoredRecipe>

    @Upsert
    suspend fun upsert(storedRecipes: List<StoredRecipe>)

    @Query("DELETE FROM recipe")
    suspend fun deleteAll()
}
