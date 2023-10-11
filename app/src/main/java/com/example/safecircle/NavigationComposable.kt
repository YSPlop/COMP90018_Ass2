package com.example.safecircle

import com.example.safecircle.utils.PreferenceHelper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.safecircle.database.Role
import com.example.safecircle.ui.screen.AboutScreen
import com.example.safecircle.ui.screen.ChildMapScreen
import com.example.safecircle.ui.screen.ChildSettingsScreen
import com.example.safecircle.ui.screen.DashboardScreen
import com.example.safecircle.ui.screen.HelpScreen
import com.example.safecircle.ui.screen.LandingScreen
import com.example.safecircle.ui.screen.LoginScreen
import com.example.safecircle.ui.screen.MapsScreen
import com.example.safecircle.ui.screen.RegisterScreen
import com.example.safecircle.ui.screen.SettingsScreen
import com.example.safecircle.viewmodel.LoginViewModel
import com.example.safecircle.viewmodel.RegisterViewModel

@Composable
fun NavigationComposable(navController: NavHostController){
    val context = LocalContext.current
    val preferenceHelper = PreferenceHelper(context)
    val familyId = preferenceHelper.getFamilyID()
    val username = preferenceHelper.getUsername()
    val role = preferenceHelper.getRole()

    val startRoute = if (!familyId.isNullOrEmpty() && !username.isNullOrEmpty()) {
        if(role == Role.PARENT){
            Dashboard.route
        }else{
            ChildMap.route
        }

    } else {
        Register.route
    }
    val registerViewModel: RegisterViewModel = remember { RegisterViewModel(context) }
    val loginViewModel: LoginViewModel = remember { LoginViewModel(context) }

    NavHost(navController = navController, startDestination = startRoute){
        composable(Register.route){
            RegisterScreen(navController, registerViewModel)
        }
        composable(Login.route){
            LoginScreen(navController, loginViewModel)
        }
        composable(Landing.route){
            LandingScreen(navController)
        }
        composable(Dashboard.route){
            DashboardScreen(navController)
        }
        composable(
            Map.routeTemplate,
            arguments = listOf(navArgument("username") { nullable = true })) {
            if (familyId != null) {
                ChildMapScreen(navController, it.arguments?.getString("username"), familyId)
            }
        }
        composable(Settings.route){
            SettingsScreen(navController)
        }
        composable(About.route){
            AboutScreen(navController)
        }
        composable(Help.route){
            HelpScreen(navController)
        }
        composable(ChildMap.route){
            ChildMapScreen(navController, username!!, familyId!!)
        }
        composable(ChildSettings.route){
            ChildSettingsScreen(navController)
        }
    }
}