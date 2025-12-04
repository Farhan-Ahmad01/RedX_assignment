package com.example.clickretina_assignment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.clickretina_assignment.ui.auth.AuthScreen
import com.example.clickretina_assignment.ui.auth.AuthViewModel
import com.example.clickretina_assignment.ui.theme.MyProfileAppTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        val profileViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]

        setContent {
            MyProfileAppTheme {
                AppNavigation(authViewModel, profileViewModel)
            }
        }
    }
}

@Composable
fun AppNavigation(authViewModel: AuthViewModel, profileViewModel: ProfileViewModel) {
    val navController = rememberNavController()
    val firebaseAuth = FirebaseAuth.getInstance()
    val startDestination = if (firebaseAuth.currentUser != null) "profile" else "auth"

    NavHost(navController = navController, startDestination = startDestination) {
        composable("auth") {
            AuthScreen(
                authViewModel = authViewModel,
                onLoginSuccess = { navController.navigate("profile") { popUpTo("auth") { inclusive = true } } },
                onSignUpSuccess = { navController.navigate("profile") { popUpTo("auth") { inclusive = true } } }
            )
        }
        composable("profile") {
            UserProfileScreen(profileViewModel) {
                authViewModel.logout()
                navController.navigate("auth") { popUpTo("profile") { inclusive = true } }
            }
        }
    }
}