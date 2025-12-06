package com.example.recipeapp.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


interface RecipeApiService {
    @GET("search.php")
    suspend fun searchRecipes(@Query("s") query: String): ApiRecipeResponse

    @GET("random.php")
    suspend fun getRandomRecipe(): ApiRecipeResponse

    @GET("lookup.php")
    suspend fun getRecipeById(@Query("i") id: String): ApiRecipeResponse

}

object RetrofitInstance {
    private const val BASE_URL = "https://www.themealdb.com/api/json/v1/1/"

    val api: RecipeApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RecipeApiService::class.java)
    }
}