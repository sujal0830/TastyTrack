package com.example.recipeapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.recipeapp.R
import com.google.firebase.auth.FirebaseAuth

// Colors
private val ScreenBackground = Color(0xFFFFF5D7)
private val DarkBrown = Color(0xFF5A3B26)
private val LogoutRed = Color(0xFFC62828)
private val CardBackground = Color.White

@Composable
fun ProfileScreen(navController: NavHostController) {
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser

    // Security check: if user is null, redirect to login
    LaunchedEffect(key1 = user) {
        if (user == null) {
            navController.navigate("login") {
                popUpTo("home") { inclusive = true }
            }
        }
    }

    if (user == null) return

    Scaffold(
        containerColor = ScreenBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title
            Text(
                text = stringResource(R.string.profile_title),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = DarkBrown,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // User Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    // Name Section
                    Text(
                        text = stringResource(R.string.profile_name_label),
                        style = MaterialTheme.typography.labelLarge,
                        color = DarkBrown.copy(alpha = 0.6f)
                    )
                    Text(
                        text = user.displayName ?: "No Name",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = DarkBrown
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Email Section
                    Text(
                        text = stringResource(R.string.profile_email_label),
                        style = MaterialTheme.typography.labelLarge,
                        color = DarkBrown.copy(alpha = 0.6f)
                    )
                    Text(
                        text = user.email ?: "No Email",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = DarkBrown
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Account Options Section
            Text(
                text = stringResource(R.string.profile_account_options),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = DarkBrown,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(16.dp))

            ProfileOptionItem(
                icon = Icons.Default.Edit,
                text = stringResource(R.string.profile_option_edit),
                onClick = { /* TODO: Navigate to edit profile */ }
            )

            Spacer(modifier = Modifier.height(12.dp))

            ProfileOptionItem(
                icon = Icons.Default.MenuBook,
                text = stringResource(R.string.profile_option_recipes),
                onClick = { navController.navigate("recipes") }
            )

            Spacer(modifier = Modifier.height(12.dp))

            ProfileOptionItem(
                icon = Icons.Default.Favorite,
                text = stringResource(R.string.profile_option_favourites),
                onClick = { navController.navigate("favourite") }
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Logout Button
            Button(
                onClick = {
                    auth.signOut()
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LogoutRed,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = stringResource(R.string.profile_logout_button),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ProfileOptionItem(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = DarkBrown
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = DarkBrown
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = DarkBrown.copy(alpha = 0.5f)
            )
        }
    }
}