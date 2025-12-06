package com.example.recipeapp.data

data class Recipe(
    val id: String = "",
    val title: String = "",
    val category: String? = null,
    val instructions: String? = null,
    val imageUrl: String? = null,
    val ingredients: List<String> = emptyList()
)