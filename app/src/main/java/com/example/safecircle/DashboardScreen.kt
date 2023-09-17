package com.example.safecircle

import android.app.Activity
import android.hardware.SensorEventListener
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@Composable
fun DashboardScreen(navController: NavHostController) {
    Column {
        Text(text = "DashboardScreen")
        Button(onClick = { navController.navigate(Login.route) }) {
            Text(text = "Log out")
        }
    }
}