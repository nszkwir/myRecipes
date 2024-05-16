package com.spitzer.data.storage.database.room.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.spitzer.data.storage.database.room.dto.StoredFavoriteRecipe

@Dao
interface FavoriteRecipeDao {

    @Query("SELECT * FROM favorite_recipe")
    fun get(): List<StoredFavoriteRecipe>

    @Upsert
    suspend fun upsert(recipe: StoredFavoriteRecipe)

    @Query("DELETE FROM favorite_recipe")
    suspend fun deleteAll()

}
