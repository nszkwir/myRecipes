package com.spitzer.data.storage.database.di

import com.spitzer.data.storage.sharedpreferences.RecipeSharedPreferences
import com.spitzer.data.storage.sharedpreferences.RecipeSharedPreferencesImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PreferencesModule {

    @Binds
    @Singleton
    internal abstract fun bindsRecipePreferences(
        recipeSharedPreferences: RecipeSharedPreferencesImpl
    ): RecipeSharedPreferences

}
