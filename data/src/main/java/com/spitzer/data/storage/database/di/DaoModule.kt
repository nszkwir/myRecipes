package com.spitzer.data.storage.database.di

import com.spitzer.data.storage.database.room.RecipeDatabase
import com.spitzer.data.storage.database.room.dao.FavoriteRecipeDao
import com.spitzer.data.storage.database.room.dao.RecipeDetailsDao
import com.spitzer.data.storage.database.room.dao.RecipesDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DaoModule {

    @Provides
    @Singleton
    fun providesLastSeenRecipesDao(
        database: RecipeDatabase,
    ): FavoriteRecipeDao = database.lastSeenRecipesDao()

    @Provides
    @Singleton
    fun providesRecipeDetailsDao(
        database: RecipeDatabase,
    ): RecipeDetailsDao = database.recipeDetailsDao()

    @Provides
    @Singleton
    fun providesRecipesDao(
        database: RecipeDatabase,
    ): RecipesDao = database.recipesDao()

}
