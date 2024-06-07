package com.spitzer.domain.usecase.recipe

import android.database.sqlite.SQLiteException
import com.spitzer.contracts.RecipeRepository
import com.spitzer.domain.utils.WrappedResult
import com.spitzer.entity.network.NetworkError
import com.spitzer.entity.search.SortCriteria
import com.spitzer.entity.search.SortOrder
import javax.inject.Inject

sealed class RefreshRecipeListUseCaseError {
    data object NoInternet : RefreshRecipeListUseCaseError()
    data object Generic : RefreshRecipeListUseCaseError()
}

class RefreshRecipeListUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(
        sortCriteria: SortCriteria,
        sortOrder: SortOrder
    ): WrappedResult<Unit, RefreshRecipeListUseCaseError> {
        return try {
            repository.refreshRecipeList(
                sortCriteria = sortCriteria,
                sortOrder = sortOrder
            )
            WrappedResult.Success(Unit)
        } catch (e: NetworkError) {
            when (e) {
                is NetworkError.NoInternet -> WrappedResult.Error(RefreshRecipeListUseCaseError.NoInternet)
                else -> WrappedResult.Error(RefreshRecipeListUseCaseError.Generic)
            }
        } catch (e: IllegalStateException) {
            WrappedResult.Error(RefreshRecipeListUseCaseError.Generic)
        } catch (e: SQLiteException) {
            WrappedResult.Error(RefreshRecipeListUseCaseError.Generic)
        } catch (e: IndexOutOfBoundsException) {
            WrappedResult.Error(RefreshRecipeListUseCaseError.Generic)
        }
    }
}
