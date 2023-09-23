package com.example.safecircle.ui.screen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.safecircle.Dashboard
import com.example.safecircle.Landing
import com.example.safecircle.Register
import com.example.safecircle.database.FamilyDatabase
import com.example.safecircle.ui.theme.CyanSecondary
import com.example.safecircle.ui.theme.YellowPrimary
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavHostController) {
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
                        val isValid = true
                        //val userDatabase = UserDatabase(
                        val familyDatabase = FamilyDatabase()
                        val childId = ""
                        // Check if family exists
                        familyDatabase.familyExists(familyID) { exists ->
                            if (exists) {
                                if (loginType == "parent") {
                                    familyDatabase.usernamePasswordMatch(
                                        username,
                                        password,
                                        familyID
                                    ) { match ->
                                        if (match) {
                                            navController.navigate(Landing.route)
                                        } else {
                                            showErrorDialog = true
                                        }
                                    }

                                } else {
                                    showErrorDialog = true
                                }
                                if (loginType == "kid") {
                                    familyDatabase.codeMatch(
                                        code,
                                        familyID
                                    ) { match ->
                                        if (match) {
                                            navController.navigate(Landing.route)
                                        } else {
                                            showErrorDialog = true
                                        }
                                    }

                                } else {
                                    showErrorDialog = true
                                }
                            }
                        }

                        //if (isValid) {
                        //    navController.navigate(Landing.route)
                        //} else {
                        //showErrorDialog = true
                        //}


                        //userDatabase.validateUser(username, password) { isValid ->
                        //    if (isValid) {
                        //        navController.navigate(Landing.route)
                        //    } else {
                        //        showErrorDialog = true
                        //    }
                        //}
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

        }
        // if login fails, show the dialog
        LoginErrorDialog(showErrorDialog) {
            showErrorDialog = false
        }
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
            text = "Don't have an account?",
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

            // Space between Register and here for better readability
            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = "here",
                color = Color.Gray,
                fontSize = 16.sp,
            )
        }
    }

}

@Composable
fun LoginErrorDialog(showErrorDialog: Boolean, onDismiss: () -> Unit) {
    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(text = "Error") },
            text = { Text("Invalid username or password.") },
            confirmButton = {
                TextButton(onClick = onDismiss) {
                    Text("OK")
                }
            }
        )
    }
}
