package com.spitzer.data.storage.database.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.spitzer.data.storage.database.room.dao.FavoriteRecipeDao
import com.spitzer.data.storage.database.room.dao.RecipeDetailsDao
import com.spitzer.data.storage.database.room.dao.RecipesDao
import com.spitzer.data.storage.database.room.dto.StoredFavoriteRecipe
import com.spitzer.data.storage.database.room.dto.StoredRecipe
import com.spitzer.data.storage.database.room.dto.StoredRecipeDetails
import com.spitzer.data.storage.database.room.typeconverters.StringListConverters

@Database(
    entities = [StoredRecipe::class, StoredRecipeDetails::class, StoredFavoriteRecipe::class],
    version = 1,
    exportSchema = true,
)
@TypeConverters(
    StringListConverters::class
)
internal abstract class RecipeDatabase : RoomDatabase() {
    abstract fun recipesDao(): RecipesDao
    abstract fun lastSeenRecipesDao(): FavoriteRecipeDao
    abstract fun recipeDetailsDao(): RecipeDetailsDao
}
