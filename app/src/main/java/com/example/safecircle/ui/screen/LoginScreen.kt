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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.safecircle.Dashboard
import com.example.safecircle.Register
import com.example.safecircle.ui.theme.CyanSecondary
import com.example.safecircle.ui.theme.YellowPrimary
import com.example.safecircle.viewmodel.LoginViewModel
import com.example.safecircle.viewmodel.RegisterViewModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


@Composable
fun LoginScreen(navController: NavHostController, viewModel: LoginViewModel) {
    var loginType by remember { mutableStateOf("parent") }
    var familyID by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showErrorDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Drawing the background
        RegisterBackgorundColor()
        // Arranging the content
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            // Header Banner with img and Text
            RegisterBanner();

            // Login Input Field
            // Login Input Field
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {

                // Radio buttons for parent/kid
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Parent", fontSize = 20.sp) // Increased font size for consistency
                    RadioButton(
                        selected = loginType == "parent",
                        onClick = { loginType = "parent" },
                        modifier = Modifier.size(32.dp) // Setting a specific size for the radio button
                    )
                    Spacer(modifier = Modifier.width(32.dp))
                    Text(text = "Kid", fontSize = 20.sp) // Increased font size for consistency
                    RadioButton(
                        selected = loginType == "kid",
                        onClick = { loginType = "kid" },
                        modifier = Modifier.size(32.dp) // Setting a specific size for the radio button
                    )
                }

                if (loginType == "parent") {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Family ID:", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = familyID,
                            onValueChange = { familyID = it },
                            placeholder = { Text("Enter family ID") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            singleLine = true
                        )
                    }
                    // Username Row
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Username:", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = username,
                            onValueChange = { username = it },
                            placeholder = { Text("Enter username") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            singleLine = true
                        )
                    }

                    // Password Row
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Password:", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            placeholder = { Text("Enter password") },
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            singleLine = true
                        )
                    }
                } else if (loginType == "kid") {
                    // FamilyID Row
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Family ID:", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = familyID,
                            onValueChange = { familyID = it },
                            placeholder = { Text("Enter family ID") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            singleLine = true
                        )
                    }

                    // Code Row
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "    Code:   ", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = code,
                            onValueChange = { code = it },
                            placeholder = { Text("Enter code") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            singleLine = true
                        )
                    }
                }

                // Login Button
                Button(
                    onClick = {
                        val familyDatabase = FamilyDatabase()
                        // Check if family exists
                        familyDatabase.familyExists(familyID) { exists ->
                            if (exists) {
                                if (loginType == "parent") {
                                    viewModel.loginAsParent(familyID, username, password) { successfulLogin ->
                                        if (successfulLogin) {
                                            navController.navigate(Landing.route)
                                        } else {
                                            showErrorDialog = true
                                        }
                                    }
                                } else if (loginType == "kid") {
                                    viewModel.loginAsKid(familyID, code) { successfulLogin ->
                                        if (successfulLogin) {
                                            navController.navigate(Landing.route)
                                        } else {
                                            showErrorDialog = true
                                        }
                                    }
                                }
                            } else {
                                showErrorDialog = true
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .shadow(elevation = 8.dp, shape = RoundedCornerShape(24.dp)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = YellowPrimary,
                        contentColor = Color.Black
                    )
                ) {
                    Text(text = "Login", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }

                // Text to display the register as an option
                RegisterText(navController);

            }


    ) {
        Text(text = "LoginScreen")
        Button(onClick = { navController.navigate(Dashboard.route) }) {
            Text(text = "Login")
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