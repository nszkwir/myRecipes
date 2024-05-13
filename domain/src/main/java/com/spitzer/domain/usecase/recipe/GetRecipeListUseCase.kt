package com.spitzer.domain.usecase.recipe

import com.spitzer.contracts.RecipeRepository
import com.spitzer.entity.recipe.RecipePage
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetRecipeListUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    operator fun invoke(): StateFlow<RecipePage> {
        return repository.recipePage
    }
}
