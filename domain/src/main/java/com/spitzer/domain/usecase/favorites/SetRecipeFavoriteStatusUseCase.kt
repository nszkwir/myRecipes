package com.spitzer.domain.usecase.favorites

import com.spitzer.contracts.RecipeRepository
import javax.inject.Inject

class SetRecipeFavoriteStatusUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(id: Long, isFavorite: Boolean) {
        return repository.setRecipeFavorite(id, isFavorite)
    }
}
