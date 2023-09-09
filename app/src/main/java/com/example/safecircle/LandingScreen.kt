package com.example.safecircle

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@Composable
fun LandingScreen(navController: NavHostController){
    Column(

    ) {
        Text(text = "LandingScreen")
        Button(onClick = { navController.navigate(Dashboard.route) }) {
            Text(text = "Start")
        }
    }

}