package com.spitzer.data.storage.sharedpreferences

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

interface RecipeSharedPreferences {
    fun getRecipeListTotalResults(): Int
    fun updateRecipeListTotalResults(totalResults: Int)
    fun clearRecipeListTotalResults()
}

class RecipeSharedPreferencesImpl @Inject constructor(
    @ApplicationContext context: Context
) : RecipeSharedPreferences {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "my_preferences"
        private const val KEY_RECIPE_LIST_TOTAL_RESULTS = "key_recipe_list_total_results"
    }

    override fun getRecipeListTotalResults() =
        sharedPreferences.getInt(KEY_RECIPE_LIST_TOTAL_RESULTS, 0)

    override fun updateRecipeListTotalResults(totalResults: Int) =
        sharedPreferences.edit().putInt(KEY_RECIPE_LIST_TOTAL_RESULTS, totalResults).apply()

    override fun clearRecipeListTotalResults() = updateRecipeListTotalResults(0)
}
