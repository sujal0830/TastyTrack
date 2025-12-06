package com.example.recipeapp.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.recipeapp.ui.components.BottomBar
import com.example.recipeapp.ui.screens.AddRecipeScreen
import com.example.recipeapp.ui.screens.FavouriteScreen
import com.example.recipeapp.ui.screens.HomeScreen
import com.example.recipeapp.ui.screens.LoginScreen
import com.example.recipeapp.ui.screens.ProfileScreen
import com.example.recipeapp.ui.screens.RecipeDetailScreen
import com.example.recipeapp.ui.screens.RecipeListScreen
import com.example.recipeapp.ui.screens.SignupScreen
import com.example.recipeapp.ui.screens.SplashScreen
import com.example.recipeapp.viewmodel.AuthViewModel
import com.example.recipeapp.viewmodel.RecipeViewModel

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    // Shared ViewModels
    val authViewModel: AuthViewModel = viewModel()
    val recipeViewModel: RecipeViewModel = viewModel()
    
    Scaffold(
        bottomBar = { BottomBar(navController = navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController, 
            startDestination = "splash",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("splash") {
                SplashScreen(navController = navController)
            }
            
            // Authentication Routes
            composable("login") {
                LoginScreen(navController = navController, viewModel = authViewModel)
            }
            composable("signup") {
                SignupScreen(navController = navController, viewModel = authViewModel)
            }

            // Main App Routes
            composable("home") {
                HomeScreen(navController = navController, viewModel = recipeViewModel)
            }
            composable("favourite") {
                FavouriteScreen(viewModel = recipeViewModel)
            }
            composable("recipes") {
                // Using RecipeListScreen for global recipes/search results
                RecipeListScreen(navController = navController, viewModel = recipeViewModel)
            }
            
            // Detail Screen Route
            composable(
                route = "recipeDetail/{recipeId}",
                arguments = listOf(navArgument("recipeId") { type = NavType.StringType })
            ) { backStackEntry ->
                val recipeId = backStackEntry.arguments?.getString("recipeId") ?: ""
                RecipeDetailScreen(
                    navController = navController,
                    recipeId = recipeId,
                    viewModel = recipeViewModel
                )
            }

            composable("add_recipe") {
                 AddRecipeScreen(
                    onSaveClicked = {
                        navController.popBackStack()
                    }
                )
            }
            composable("profile") {
                ProfileScreen(navController = navController)
            }
        }
    }
}