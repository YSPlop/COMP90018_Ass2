package com.example.safecircle.ui.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.safecircle.database.FamilyDatabase
import com.example.safecircle.Landing
import com.example.safecircle.Login
import com.example.safecircle.R
import com.example.safecircle.viewmodel.RegisterViewModel
import com.example.safecircle.ui.theme.CyanSecondary
import com.example.safecircle.ui.theme.YellowPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavHostController, viewModel: RegisterViewModel) {
    var familyID by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var showUsernameDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Drawing the background
        RegisterBackgorundColor()

        // Arranging the content
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            // Header Banner with img and Text
            RegisterBanner();

            // Register Input Field
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

                // Register Button
                Button(
                    onClick = {
                        val familyDatabase = FamilyDatabase()

                        // Check if family exists
                        familyDatabase.familyExists(familyID) { exists ->
                            if (exists) {
                                showDialog = true
                            }
                            else{
                                familyDatabase.userNameExists(familyID,username) { exists ->
                                    if (exists) {
                                        showUsernameDialog = true
                                    }
                                    else{
                                        viewModel.register(familyID, username, password)
                                        navController.navigate(Landing.route);
                                    }
                                }
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
                    Text(text = "Register", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }

                // Text to display the login as a option
                LoginText(navController);

            }

        }
        // if family ID already exist, show the dialog
        FamilyExistDialog(showDialog) {
            showDialog = false
        }
        UsernameExistDialog(showUsernameDialog){
            showUsernameDialog = false
        }
    }
}


@Composable
fun RegisterBackgorundColor() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // Draw first half with Color(0xFF66D2D6)
        drawPath(
            path = Path().apply {
                moveTo(0f, 0f)
                lineTo(width, 0f)
                lineTo(0f, 0.25f * height)
                close()
            },
            color = CyanSecondary
        )

        // Draw second half with Color.White
        drawPath(
            path = Path().apply {
                moveTo(0f, 0.25f * height)
                lineTo(width, 0f)
                lineTo(width, height)
                lineTo(0f, height)
                close()
            },
            color = YellowPrimary
        )
    }
}

@Composable
fun RegisterBanner() {
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = "SafeCircle",
        color = Color(0xFFFFD567),
        fontSize = 32.sp,
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.ExtraBold
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = "Your Peace of Mind \n" + "in Every Step They Take",
        color = Color.White,
        fontSize = 18.sp,
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.ExtraBold
    )

    Image(
        painter = painterResource(id = R.drawable.family),
        contentDescription = "family",
        modifier = Modifier.size(128.dp)
    )
}

@Composable
fun LoginText(navController: NavHostController) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Normal Text
        Text(
            text = "Already have an account?",
            color = Color.Gray,
            fontSize = 16.sp
        )

        // Inline clickable and non-clickable texts
        Row(
            horizontalArrangement = Arrangement.Center
        ) {
            ClickableText(
                text = AnnotatedString("Login", SpanStyle(color = CyanSecondary, fontWeight = FontWeight.Bold)),
                onClick = { offset ->
                    navController.navigate(Login.route)
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

@Composable
fun FamilyExistDialog(showDialog: Boolean, onDismiss: () -> Unit) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(text = "Error") },
            text = { Text("Family ID already exists.") },
            confirmButton = {
                TextButton(onClick = onDismiss) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
fun UsernameExistDialog(showDialog: Boolean, onDismiss: () -> Unit) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(text = "Error") },
            text = { Text("Username already exists.") },
            confirmButton = {
                TextButton(onClick = onDismiss) {
                    Text("OK")
                }
            }
        )
    }
}