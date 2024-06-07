package com.spitzer.domain.usecase.recipe

import android.database.sqlite.SQLiteException
import com.spitzer.contracts.RecipeRepository
import com.spitzer.domain.utils.WrappedResult
import com.spitzer.entity.network.NetworkError
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
        } catch (e: NetworkError) {
            WrappedResult.Error(FetchNextRecipePageWhenNeededError.Retry)
        } catch (e: IllegalStateException) {
            WrappedResult.Error(FetchNextRecipePageWhenNeededError.Retry)
        } catch (e: SQLiteException) {
            WrappedResult.Error(FetchNextRecipePageWhenNeededError.Retry)
        } catch (e: IndexOutOfBoundsException) {
            WrappedResult.Error(FetchNextRecipePageWhenNeededError.Retry)
        }
    }
}
