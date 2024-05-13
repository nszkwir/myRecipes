package com.spitzer.domain.usecase.recipe

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
        } catch (e: NetworkError.NoInternet) {
            WrappedResult.Error(RefreshRecipeListUseCaseError.NoInternet)
        } catch (e: Throwable) {
            WrappedResult.Error(RefreshRecipeListUseCaseError.Generic)
        }
    }
}
