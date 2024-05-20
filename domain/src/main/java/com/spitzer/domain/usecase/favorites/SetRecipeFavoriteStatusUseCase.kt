package com.spitzer.domain.usecase.favorites

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
        } catch (e: Throwable) {
            WrappedResult.Error(SetRecipeFavoriteStatusUseCaseError.Generic)
        }
    }
}
