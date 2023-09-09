package com.example.safecircle

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun NavigationComposable(navController: NavHostController){
    val startRoute = Register.route
    NavHost(navController = navController, startDestination = startRoute){
        composable(Register.route){
            RegisterScreen(navController)
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