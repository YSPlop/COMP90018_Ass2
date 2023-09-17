package com.example.safecircle

import com.example.safecircle.utils.PreferenceHelper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.safecircle.ui.screen.AccountSettingsScreen
import com.example.safecircle.ui.screen.ConnectionSettingsScreen
import com.example.safecircle.ui.screen.DashboardScreen
import com.example.safecircle.ui.screen.LandingScreen
import com.example.safecircle.ui.screen.LoginScreen
import com.example.safecircle.ui.screen.RegisterScreen
import com.example.safecircle.viewmodel.RegisterViewModel

@Composable
fun NavigationComposable(navController: NavHostController){
    val context = LocalContext.current
    val preferenceHelper = PreferenceHelper(context)
    val familyId = preferenceHelper.getFamilyID()
    val username = preferenceHelper.getUsername()

    val startRoute = if (!familyId.isNullOrEmpty() && !username.isNullOrEmpty()) {
        Dashboard.route
    } else {
        Register.route
    }

    val registerViewModel: RegisterViewModel = remember { RegisterViewModel(context) }

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
        composable(AccountSettings.route){
            AccountSettingsScreen(navController)
        }
        composable(ConnectionSettings.route){
            ConnectionSettingsScreen(navController)
        }
    }
}