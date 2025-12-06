package com.example.recipeapp.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class RecipeRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val api = RetrofitInstance.api

    // API

    suspend fun fetchRecipesFromApi(query: String): List<Recipe> {
        return try {
            val response = api.searchRecipes(query)
            response.meals?.map { it.toRecipe() } ?: emptyList()
        } catch (e: Exception) {
            Log.e("RecipeRepository", "Error fetching API recipes: ${e.message}")
            emptyList()
        }
    }

    suspend fun fetchRandomRecipesFromApi(count: Int): List<Recipe> = coroutineScope {
        (1..count).map {
            async {
                try {
                    val response = api.getRandomRecipe()
                    response.meals?.firstOrNull()?.toRecipe()
                } catch (e: Exception) {
                    Log.e("RecipeRepository", "Error fetching random recipe: ${e.message}")
                    null
                }
            }
        }.awaitAll().filterNotNull()
    }

    suspend fun fetchRecipeByIdFromApi(id: String): Recipe? {
        return try {
            val response = api.getRecipeById(id)
            response.meals?.firstOrNull()?.toRecipe()
        } catch (e: Exception) {
            Log.e("RecipeRepository", "Error fetching recipe by ID from API: ${e.message}")
            null
        }
    }

    // Firestore: Recipes

    suspend fun saveRecipesToFirestore(recipes: List<Recipe>) {
        val batch = firestore.batch()
        recipes.forEach { recipe ->
            val docRef = firestore.collection("recipes").document(recipe.id)
            val data = mapOf(
                "id" to recipe.id,
                "title" to recipe.title,
                "category" to recipe.category,
                "instructions" to recipe.instructions,
                "imageUrl" to recipe.imageUrl,
                "ingredients" to recipe.ingredients,
                "timestamp" to System.currentTimeMillis()
            )
            batch.set(docRef, data, SetOptions.merge())
        }
        try {
            batch.commit().await()
        } catch (e: Exception) {
            Log.e("RecipeRepository", "Error saving to Firestore: ${e.message}")
        }
    }

    fun getGlobalRecipes(): Flow<List<Recipe>> = callbackFlow {
        val listener = firestore.collection("recipes")
            .orderBy("timestamp") // latest last – you can reverse in ViewModel if needed
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("RecipeRepository", "Error listening to recipes: ${error.message}")
                    close(error)
                    return@addSnapshotListener
                }

                val recipes = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        Recipe(
                            id = doc.getString("id") ?: "",
                            title = doc.getString("title") ?: "",
                            category = doc.getString("category"),
                            instructions = doc.getString("instructions"),
                            imageUrl = doc.getString("imageUrl"),
                            ingredients = (doc.get("ingredients") as? List<String>) ?: emptyList()
                        )
                    } catch (e: Exception) {
                        null
                    }
                } ?: emptyList()
                trySend(recipes)
            }

        awaitClose { listener.remove() }
    }

    suspend fun getRecipeByIdFromFirestore(id: String): Recipe? {
        return try {
            val doc = firestore.collection("recipes").document(id).get().await()
            if (doc.exists()) {
                Recipe(
                    id = doc.getString("id") ?: "",
                    title = doc.getString("title") ?: "",
                    category = doc.getString("category"),
                    instructions = doc.getString("instructions"),
                    imageUrl = doc.getString("imageUrl"),
                    ingredients = (doc.get("ingredients") as? List<String>) ?: emptyList()
                )
            } else null
        } catch (e: Exception) {
            Log.e("RecipeRepository", "Error getting recipe by ID from Firestore: ${e.message}")
            null
        }
    }

    //  Firestore: Favourites

    suspend fun toggleFavourite(recipeId: String) {
        val userId = auth.currentUser?.uid ?: return
        val favRef = firestore.collection("users").document(userId)
            .collection("favourites").document(recipeId)

        try {
            val doc = favRef.get().await()
            if (doc.exists()) {
                favRef.delete().await()
            } else {
                favRef.set(
                    mapOf(
                        "recipeId" to recipeId,
                        "addedAt" to System.currentTimeMillis()
                    )
                ).await()
            }
        } catch (e: Exception) {
            Log.e("RecipeRepository", "Error toggling favourite: ${e.message}")
        }
    }

    fun getUserFavourites(): Flow<List<String>> = callbackFlow {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            trySend(emptyList())
            awaitClose {}
            return@callbackFlow
        }

        val listener = firestore.collection("users").document(userId)
            .collection("favourites")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("RecipeRepository", "Error listening to favourites: ${error.message}")
                    close(error)
                    return@addSnapshotListener
                }
                val ids = snapshot?.documents?.map { it.id } ?: emptyList()
                trySend(ids)
            }

        awaitClose { listener.remove() }
    }
}
