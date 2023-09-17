package com.example.safecircle.ui.screen

import com.example.safecircle.utils.PreferenceHelper
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.safecircle.Dashboard

@Composable
fun LandingScreen(navController: NavHostController) {
    // Use the LocalContext to get the current context within Compose
    val context = LocalContext.current

    // Create a com.example.safecircle.utils.PreferenceHelper instance using the current context
    val preferenceHelper = PreferenceHelper(context)

    // Retrieve the familyID and username from com.example.safecircle.utils.PreferenceHelper
    val familyID = preferenceHelper.getFamilyID()
    val username = preferenceHelper.getUsername()

    Column(
        modifier = Modifier.padding(16.dp) // Add some padding around the elements for better visual appearance
    ) {
        Text(text = "Family ID: $familyID")
        Text(text = "Username: $username")

        Button(onClick = { navController.navigate(Dashboard.route) }) {
            Text(text = "Start")
        }
    }
}