package com.example.recipeapp.viewmodel

import com.example.recipeapp.data.Recipe

data class RecipeUiState(
    val loading: Boolean = false,

    // Global recipes loaded from Firestore
    val recipes: List<Recipe> = emptyList(),

    // Search results (kept separate from global list)
    val searchResults: List<Recipe> = emptyList(),

    // Random suggestions for Home
    val randomRecipes: List<Recipe> = emptyList(),

    // Favourite recipe IDs
    val favouriteIds: List<String> = emptyList(),

    // Detail screen data
    val selectedRecipe: Recipe? = null,
    val detailLoading: Boolean = false,
    val detailError: String? = null,

    // When true, RecipeListScreen shows searchResults instead of recipes
    val searchMode: Boolean = false,

    // Generic error (mostly for search/list)
    val errorMessage: String? = null
)
