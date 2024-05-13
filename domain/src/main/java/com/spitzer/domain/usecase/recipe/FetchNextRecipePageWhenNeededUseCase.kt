package com.spitzer.domain.usecase.recipe

import com.spitzer.contracts.RecipeRepository
import com.spitzer.domain.utils.WrappedResult
import com.spitzer.entity.search.SortCriteria
import com.spitzer.entity.search.SortOrder
import javax.inject.Inject

sealed class FetchNextRecipePageWhenNeededError {
    data object Retry : FetchNextRecipePageWhenNeededError()
}

class FetchNextRecipePageWhenNeededUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(
        elementIndex: Int,
        sortCriteria: SortCriteria,
        sortOrder: SortOrder
    ): WrappedResult<Unit, FetchNextRecipePageWhenNeededError> {
        return try {
            repository.fetchRecipeList(
                elementIndex = elementIndex,
                sortCriteria = sortCriteria,
                sortOrder = sortOrder
            )
            WrappedResult.Success(Unit)
        } catch (e: Throwable) {
            WrappedResult.Error(FetchNextRecipePageWhenNeededError.Retry)
        }
    }
}