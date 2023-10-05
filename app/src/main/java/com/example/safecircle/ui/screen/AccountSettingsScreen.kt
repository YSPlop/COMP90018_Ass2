package com.example.safecircle.ui.screen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.safecircle.Login
import com.example.safecircle.database.Child
import com.example.safecircle.database.FamilyDatabase
import com.example.safecircle.ui.components.AppDrawer
import com.example.safecircle.ui.components.AppTopBar
import com.example.safecircle.utils.PreferenceHelper

@Composable
fun AccountSettingsScreen(navController: NavHostController) {
    val context = LocalContext.current
    val preferenceHelper = PreferenceHelper(context)

    val familyDatabase = FamilyDatabase()

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val familyId = preferenceHelper.getFamilyID()
    val username = preferenceHelper.getUsername()
    val childName = remember { mutableStateOf(TextFieldValue("")) }
    val childCode = remember { mutableStateOf(TextFieldValue("")) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = { AppDrawer(drawerState, navController) }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top bar at the top
            AppTopBar(drawerState, "Account Settings")

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(text= familyId.toString())
                Text(text = username.toString())
                Text(text = "Create Account for Child", modifier = Modifier.padding(8.dp))

                OutlinedTextField(
                    value = childName.value,
                    onValueChange = { childName.value = it },
                    label = { Text("Child Name") }
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = childCode.value,
                    onValueChange = { childCode.value = it },
                    label = { Text("Child Code") }
                )
                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    Log.i("test", "onclick called: famID: $familyId")
                    familyId?.let {
                        val newChild = Child(childName.value.text, childCode.value.text)
                        familyDatabase.addChildToFamily(it, newChild)
                        // Displaying the Toast
                        Toast.makeText(
                            context,
                            "Account ${childName.value.text} has been created",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Clear the textfields
                        childName.value = TextFieldValue("")
                        childCode.value = TextFieldValue("")
                    }
                }) {
                    Text(text = "Create Account")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    navController.navigate(Login.route)
                    preferenceHelper.clearPreferences()
                }) {
                    Text(text = "Log out")
                }

            }
        }
    }
}

