package com.example.recipeapp.ui.components

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

// Data class for Bottom Navigation Items
data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

@Composable
fun BottomBar(navController: NavHostController) {
    val screens = listOf(
        BottomNavItem("Home", Icons.Default.Home, "home"),
        BottomNavItem("Favourite", Icons.Default.Favorite, "favourite"),
        BottomNavItem("Recipe", Icons.Default.MenuBook, "recipes"),
        BottomNavItem("Profile", Icons.Default.Person, "profile")
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Check if the current screen is one of the bottom nav items
    val showBottomBar = screens.any { it.route == currentDestination?.route }

    if (showBottomBar) {
        NavigationBar(
            containerColor = Color.White,
            contentColor = Color(0xFF5A3B26),
            modifier = Modifier.navigationBarsPadding()
        ) {
            screens.forEach { screen ->
                val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                
                NavigationBarItem(
                    icon = { Icon(screen.icon, contentDescription = screen.label) },
                    label = { Text(screen.label) },
                    selected = isSelected,
                    onClick = {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFFC62828),
                        selectedTextColor = Color(0xFFC62828),
                        unselectedIconColor = Color(0xFF5A3B26),
                        unselectedTextColor = Color(0xFF5A3B26),
                        indicatorColor = Color.Transparent // Optional: remove the indicator pill
                    )
                )
            }
        }
    }
}