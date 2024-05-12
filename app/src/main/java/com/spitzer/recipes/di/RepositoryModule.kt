package com.spitzer.recipes.di

import com.spitzer.contracts.RecipeRepository
import com.spitzer.data.repository.RecipeRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    internal abstract fun bindsRecipeRepository(
        recipeRepository: RecipeRepositoryImpl
    ): RecipeRepository
}
