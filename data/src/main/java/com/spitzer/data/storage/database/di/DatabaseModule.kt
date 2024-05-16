package com.spitzer.data.storage.database.di

import android.content.Context
import androidx.room.Room
import com.spitzer.data.storage.database.room.RecipeDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {

    @Provides
    @Singleton
    fun providesRecipeDatabase(
        @ApplicationContext context: Context,
    ): RecipeDatabase = Room.databaseBuilder(
        context,
        RecipeDatabase::class.java,
        "recipe-database",
    ).build()

}
