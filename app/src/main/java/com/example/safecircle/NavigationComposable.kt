package com.example.safecircle

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun NavigationComposable(navController: NavHostController){
    val context = LocalContext.current
    val registerViewModel: RegisterViewModel = remember { RegisterViewModel(context) }
    val startRoute = Register.route
    NavHost(navController = navController, startDestination = startRoute){
        composable(Register.route){
            RegisterScreen(navController, registerViewModel)
        }
        composable(Login.route){
            LoginScreen(navController)
        }
        composable(Landing.route){
            LandingScreen(navController)
        }
        composable(Dashboard.route){
            DashboardScreen(navController)
        }

    }
}