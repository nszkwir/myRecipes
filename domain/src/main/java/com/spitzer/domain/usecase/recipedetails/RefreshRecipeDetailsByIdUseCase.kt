package com.spitzer.domain.usecase.recipedetails

import android.database.sqlite.SQLiteException
import com.spitzer.contracts.RecipeRepository
import com.spitzer.domain.utils.WrappedResult
import com.spitzer.entity.network.NetworkError
import com.spitzer.entity.recipe.RecipeDetails
import javax.inject.Inject

sealed class RefreshRecipeDetailsByIdUseCaseError {
    data object NoInternet : RefreshRecipeDetailsByIdUseCaseError()
    data object Generic : RefreshRecipeDetailsByIdUseCaseError()
}

class RefreshRecipeDetailsByIdUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(
        id: Long
    ): WrappedResult<RecipeDetails, RefreshRecipeDetailsByIdUseCaseError> {
        return try {
            val recipeDetails = repository.fetchRecipeDetails(id = id)
            WrappedResult.Success(recipeDetails)
        } catch (e: SQLiteException) {
            WrappedResult.Error(RefreshRecipeDetailsByIdUseCaseError.Generic)
        } catch (e: IndexOutOfBoundsException) {
            WrappedResult.Error(RefreshRecipeDetailsByIdUseCaseError.Generic)
        } catch (e: NetworkError) {
            when (e) {
                is NetworkError.NoInternet -> WrappedResult.Error(
                    RefreshRecipeDetailsByIdUseCaseError.NoInternet
                )

                else -> WrappedResult.Error(RefreshRecipeDetailsByIdUseCaseError.Generic)
            }
        }
    }
}
