package com.example.safecircle.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.safecircle.Dashboard
import com.example.safecircle.Register
import com.example.safecircle.ui.theme.CyanSecondary

@Composable
fun LoginScreen(navController: NavHostController){
    Column(

    ) {
        Text(text = "LoginScreen")
        Button(onClick = { navController.navigate(Dashboard.route) }) {
            Text(text = "Login")
        }
        Button(onClick = { navController.navigate(MapChild.route) }) {
            Text(text = "MapChild")
        }
        RegisterText(navController = navController)
    }

}

@Composable
fun RegisterText(navController: NavHostController) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Normal Text
        Text(
            text = "Do not have an account? ",
            color = Color.Gray,
            fontSize = 16.sp
        )

        // Inline clickable and non-clickable texts
        Row(
            horizontalArrangement = Arrangement.Center
        ) {
            ClickableText(
                text = AnnotatedString("Register", SpanStyle(color = CyanSecondary, fontWeight = FontWeight.Bold)),
                onClick = { offset ->
                    navController.navigate(Register.route)
                },
                style = TextStyle(color = CyanSecondary, fontSize = 16.sp)
            )

            // Space between Login and here for better readability
            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = "here",
                color = Color.Gray,
                fontSize = 16.sp,
            )
        }
    }

}