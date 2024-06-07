package com.spitzer.domain.usecase.favorites

import android.database.sqlite.SQLiteException
import com.spitzer.contracts.RecipeRepository
import com.spitzer.domain.utils.WrappedResult
import javax.inject.Inject

sealed class SetRecipeFavoriteStatusUseCaseError {
    data object Generic : SetRecipeFavoriteStatusUseCaseError()
}

class SetRecipeFavoriteStatusUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(
        id: Long,
        isFavorite: Boolean
    ): WrappedResult<Unit, SetRecipeFavoriteStatusUseCaseError> {
        return try {
            repository.setRecipeFavorite(id, isFavorite)
            WrappedResult.Success(Unit)
        } catch (e: SQLiteException) {
            WrappedResult.Error(SetRecipeFavoriteStatusUseCaseError.Generic)
        } catch (e: IndexOutOfBoundsException) {
            WrappedResult.Error(SetRecipeFavoriteStatusUseCaseError.Generic)
        }
    }
}
