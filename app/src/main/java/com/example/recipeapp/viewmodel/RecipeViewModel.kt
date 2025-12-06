package com.example.recipeapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipeapp.data.Recipe
import com.example.recipeapp.data.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class RecipeViewModel : ViewModel() {

    private val repository = RecipeRepository()

    private val _uiState = MutableStateFlow(RecipeUiState())
    val uiState: StateFlow<RecipeUiState> = _uiState.asStateFlow()

    init {
        observeGlobalRecipes()
        observeFavourites()
    }

    //  Global recipes listener

    private fun observeGlobalRecipes() {
        repository.getGlobalRecipes()
            .onEach { recipes ->
                _uiState.value = _uiState.value.copy(
                    recipes = recipes,
                    loading = false
                )
            }
            .launchIn(viewModelScope)
    }

    private fun observeFavourites() {
        repository.getUserFavourites()
            .onEach { favIds ->
                _uiState.value = _uiState.value.copy(favouriteIds = favIds)
            }
            .launchIn(viewModelScope)
    }

    //  Random recipes for Home

    fun loadRandomRecipes(count: Int = 4) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true)
            val randomList = repository.fetchRandomRecipesFromApi(count)
            _uiState.value = _uiState.value.copy(
                loading = false,
                randomRecipes = randomList
            )
        }
    }

    //  Search

    fun searchRecipes(query: String) {
        if (query.isBlank()) {
            // Clear search mode, go back to global recipes
            _uiState.value = _uiState.value.copy(
                searchMode = false,
                searchResults = emptyList(),
                errorMessage = null
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                loading = true,
                searchMode = true,
                errorMessage = null
            )

            val apiRecipes = repository.fetchRecipesFromApi(query)

            if (apiRecipes.isNotEmpty()) {
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    searchResults = apiRecipes,
                    errorMessage = null
                )

                // Optional: still sync to Firestore as global library
                repository.saveRecipesToFirestore(apiRecipes)
            } else {
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    searchResults = emptyList(),
                    errorMessage = "No recipes found for \"$query\""
                )
            }
        }
    }

    fun clearSearch() {
        _uiState.value = _uiState.value.copy(
            searchMode = false,
            searchResults = emptyList(),
            errorMessage = null
        )
    }

    //  Detail loading

    fun loadRecipeDetail(recipeId: String) {
        viewModelScope.launch {
            // If already loaded in any list, reuse it
            val current = findRecipeInState(recipeId)
            if (current != null) {
                _uiState.value = _uiState.value.copy(
                    selectedRecipe = current,
                    detailLoading = false,
                    detailError = null
                )
                return@launch
            }

            _uiState.value = _uiState.value.copy(
                detailLoading = true,
                detailError = null
            )

            // Try Firestore
            val fromDb = repository.getRecipeByIdFromFirestore(recipeId)
            if (fromDb != null) {
                _uiState.value = _uiState.value.copy(
                    selectedRecipe = fromDb,
                    detailLoading = false,
                    detailError = null
                )
                return@launch
            }

            // Try API
            val fromApi = repository.fetchRecipeByIdFromApi(recipeId)
            if (fromApi != null) {
                _uiState.value = _uiState.value.copy(
                    selectedRecipe = fromApi,
                    detailLoading = false,
                    detailError = null
                )
                // Optionally cache into Firestore
                repository.saveRecipesToFirestore(listOf(fromApi))
            } else {
                _uiState.value = _uiState.value.copy(
                    detailLoading = false,
                    detailError = "Recipe not found"
                )
            }
        }
    }

    private fun findRecipeInState(recipeId: String): Recipe? {
        val state = _uiState.value
        return state.recipes.find { it.id == recipeId }
            ?: state.randomRecipes.find { it.id == recipeId }
            ?: state.searchResults.find { it.id == recipeId }
    }

    //  Favourites

    fun toggleFavourite(recipeId: String) {
        viewModelScope.launch {
            repository.toggleFavourite(recipeId)
        }
    }
}
