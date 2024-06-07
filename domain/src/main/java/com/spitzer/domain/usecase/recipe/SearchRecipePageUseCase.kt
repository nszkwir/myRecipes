package com.spitzer.domain.usecase.recipe

import com.spitzer.contracts.RecipeRepository
import com.spitzer.domain.utils.WrappedResult
import com.spitzer.entity.network.NetworkError
import com.spitzer.entity.recipe.Recipe
import com.spitzer.entity.search.SearchCriteria
import com.spitzer.entity.search.SortCriteria
import com.spitzer.entity.search.SortOrder
import javax.inject.Inject

sealed class SearchRecipePageUseCaseError {
    data object NoInternet : SearchRecipePageUseCaseError()
    data object Generic : SearchRecipePageUseCaseError()
}

class SearchRecipePageUseCase @Inject constructor(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(
        query: String,
        searchCriteria: SearchCriteria,
        sortCriteria: SortCriteria,
        sortOrder: SortOrder
    ): WrappedResult<List<Recipe>, SearchRecipePageUseCaseError> {
        return try {
            val result = repository.searchRecipeList(
                query = query,
                searchCriteria = searchCriteria,
                sortCriteria = sortCriteria,
                sortOrder = sortOrder
            )
            WrappedResult.Success(result)
        } catch (e: NetworkError.NotFound) {
            WrappedResult.Success(emptyList())
        } catch (e: NetworkError.NoInternet) {
            WrappedResult.Error(SearchRecipePageUseCaseError.NoInternet)
        }

    }
}