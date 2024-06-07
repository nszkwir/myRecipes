package com.spitzer.data.remote.api.recipe

import com.spitzer.data.di.AppDispatchers
import com.spitzer.data.di.Dispatcher
import com.spitzer.data.remote.api.recipe.dto.RecipeDetailsResponse
import com.spitzer.data.remote.api.recipe.dto.RecipePageResponse
import com.spitzer.entity.network.NetworkError
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.net.ConnectException
import javax.inject.Inject

interface RecipeAPIService {

    @GET("recipes/complexSearch")
    suspend fun fetchRecipes(
        @Query("offset") offset: Int,
        @Query("number") limit: Int,
        @Query("query") query: String?,
        @Query("includeIngredients") includeIngredients: String?,
        @Query("addRecipeInformation") addRecipeInformation: Boolean,
        @Query("sort") sort: String?,
        @Query("sortDirection") sortDirection: String
    ): Response<RecipePageResponse>

    @GET("recipes/{id}/information")
    suspend fun fetchRecipeDetails(
        @Path("id") id: Long,
        @Query("includeNutrition") includeNutrition: Boolean = false,
        @Query("addWinePairing") addWinePairing: Boolean = false,
        @Query("addTasteData") addTasteData: Boolean = false,
    ): Response<RecipeDetailsResponse>
}

interface RecipeService {
    suspend fun fetchRecipes(
        offset: Int,
        limit: Int,
        query: String?,
        includeIngredients: String?,
        sortCriteria: String?,
        sortOrder: String
    ): RecipePageResponse

    suspend fun fetchRecipeDetails(
        id: Long
    ): RecipeDetailsResponse
}

internal class RecipeServiceImpl @Inject constructor(
    private val apiService: RecipeAPIService,
    @Dispatcher(AppDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) : RecipeService {
    override suspend fun fetchRecipes(
        offset: Int,
        limit: Int,
        query: String?,
        includeIngredients: String?,
        sortCriteria: String?,
        sortOrder: String
    ): RecipePageResponse {
        return withContext(ioDispatcher) {
            try {
                val response = apiService.fetchRecipes(
                    offset = offset,
                    limit = limit,
                    query = query,
                    includeIngredients = includeIngredients,
                    addRecipeInformation = true,
                    sort = sortCriteria,
                    sortDirection = sortOrder
                )
                if (response.isSuccessful) {
                    response.body() ?: throw NetworkError.Unknown
                } else {
                    throw NetworkError.Unknown
                }
            } catch (e: ConnectException) {
                throw NetworkError.NoInternet
            }
        }
    }

    override suspend fun fetchRecipeDetails(id: Long): RecipeDetailsResponse {
        return withContext(ioDispatcher) {
            try {
                val response = apiService.fetchRecipeDetails(
                    id = id
                )
                if (response.isSuccessful) {
                    response.body() ?: throw NetworkError.Unknown
                } else {
                    throw NetworkError.Unknown
                }
            } catch (e: ConnectException) {
                throw NetworkError.NoInternet
            }
        }
    }
}
