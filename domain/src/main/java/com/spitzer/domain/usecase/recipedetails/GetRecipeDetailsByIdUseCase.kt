package com.spitzer.domain.usecase.recipedetails

import android.database.sqlite.SQLiteException
import com.spitzer.contracts.RecipeRepository
import com.spitzer.domain.utils.WrappedResult
import com.spitzer.entity.network.NetworkError
import com.spitzer.entity.recipe.RecipeDetails
import javax.inject.Inject

sealed class GetRecipeDetailsByIdUseCaseError {
    data object NoInternet : GetRecipeDetailsByIdUseCaseError()
    data object Generic : GetRecipeDetailsByIdUseCaseError()
}

class GetRecipeDetailsByIdUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(
        id: Long
    ): WrappedResult<RecipeDetails, GetRecipeDetailsByIdUseCaseError> {
        return try {
            val recipeDetails = repository.getRecipeDetailsById(id = id)
            WrappedResult.Success(recipeDetails)
        } catch (e: SQLiteException) {
            WrappedResult.Error(GetRecipeDetailsByIdUseCaseError.Generic)
        } catch (e: IndexOutOfBoundsException) {
            WrappedResult.Error(GetRecipeDetailsByIdUseCaseError.Generic)
        } catch (e: NetworkError) {
            when (e) {
                is NetworkError.NoInternet -> WrappedResult.Error(GetRecipeDetailsByIdUseCaseError.NoInternet)
                else -> WrappedResult.Error(GetRecipeDetailsByIdUseCaseError.Generic)
            }
        }
    }
}
