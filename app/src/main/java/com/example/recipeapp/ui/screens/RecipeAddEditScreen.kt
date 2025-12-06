package com.example.recipeapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recipeapp.R

// Colors
private val ScreenBackground = Color(0xFFFFF5D7)
private val DarkBrown = Color(0xFF5A3B26)
private val AccentRed = Color(0xFFC62828)

@Composable
fun RecipeAddEditScreen(onAddRecipeClicked: () -> Unit) {
    Scaffold(
        containerColor = ScreenBackground,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddRecipeClicked,
                containerColor = AccentRed,
                contentColor = Color.White,
                icon = { Icon(Icons.Default.Add, contentDescription = "Add") },
                text = { Text("Add Recipe", fontWeight = FontWeight.Bold) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            // Title
            Text(
                text = "Recipies",
                color = DarkBrown,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Recipe List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(24.dp),
                contentPadding = PaddingValues(bottom = 80.dp) // Extra padding for FAB
            ) {
                items(getSampleRecipes()) { recipe ->
                    RecipeCardItem(recipe)
                }
            }
        }
    }
}

@Composable
fun RecipeCardItem(recipe: RecipeItem) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            // Featured Image
            Image(
                painter = painterResource(id = R.drawable.splash_bg), // Placeholder
                contentDescription = recipe.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Text Content
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = recipe.title,
                        color = DarkBrown,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "Ingredients:",
                        color = DarkBrown,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    recipe.ingredients.forEach { ingredient ->
                        Text(
                            text = "• $ingredient",
                            color = DarkBrown,
                            fontSize = 14.sp
                        )
                    }
                }

                // Heart Icon
                IconButton(
                    onClick = { /* Toggle Favourite */ },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Favourite",
                        tint = AccentRed
                    )
                }
            }
        }
    }
}

data class RecipeItem(
    val title: String,
    val ingredients: List<String>
)

fun getSampleRecipes(): List<RecipeItem> {
    return listOf(
        RecipeItem(
            "Chocolate Cake",
            listOf("Flour", "Cocoa Powder", "Sugar", "Eggs", "Milk")
        ),
        RecipeItem(
            "Avocado Toast",
            listOf("Whole Grain Bread", "Ripe Avocado", "Salt & Pepper", "Chili Flakes")
        ),
        RecipeItem(
            "Berry Smoothie",
            listOf("Mixed Berries", "Yogurt", "Honey", "Ice Cubes")
        )
    )
}

@Preview
@Composable
fun RecipeAddEditScreenPreview() {
    RecipeAddEditScreen(onAddRecipeClicked = {})
}