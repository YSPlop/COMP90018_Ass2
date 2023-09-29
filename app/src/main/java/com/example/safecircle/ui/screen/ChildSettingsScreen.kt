package com.example.safecircle.ui.screen

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.safecircle.utils.PreferenceHelper
import androidx.compose.material.Text
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import com.example.safecircle.ChildMap
import com.example.safecircle.Login
import com.example.safecircle.database.FamilyDatabase
import com.example.safecircle.database.Parent
import com.example.safecircle.database.PersonalDetails

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChildSettingsScreen(navController: NavHostController) {
    val context = LocalContext.current
    val preferenceHelper = PreferenceHelper(context)
    val familyID = preferenceHelper.getFamilyID()
    val username = preferenceHelper.getUsername()

    // State to store parents' details and selected emergency contact
    val (parentsList, setParentsList) = remember { mutableStateOf(listOf<PersonalDetails>()) }
    val (selectedEmergencyContact, setSelectedEmergencyContact) = remember { mutableStateOf<String?>(null) }
    val emergencyContactNumber = preferenceHelper.getEmergencyContact()

    val familyDatabase = FamilyDatabase()

    // Fetch parents' details on screen load
    LaunchedEffect(Unit) {
        familyDatabase.getAllParentsDetails(familyID!!) { parents ->
            Log.i("test", "$parents")
            setParentsList(parents)
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // TopAppBar
        TopAppBar(
            title = { androidx.compose.material3.Text(username.toString()) },
            navigationIcon = {
                IconButton(onClick = {navController.navigate(ChildMap.route)}) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Child Navigation", Modifier.size(36.dp))
                }
            }
        )
        Divider()
        Row {
            Text(text = "emergency contact number: ")
            Text(text = emergencyContactNumber.toString())
        }
        // Display parents' details as cards
        parentsList.forEach { parent ->
            Card(modifier = Modifier.padding(24.dp)) {
                Column {
                    Text(text = parent.firstName)
                    Text(text = parent.lastName)
                    Text(text = parent.address)
                    Text(text = parent.phoneNumber)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            // Set the person's phone number as emergency contact
                            setSelectedEmergencyContact(parent.phoneNumber)
                            preferenceHelper.setEmergencyContact(parent.phoneNumber)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedEmergencyContact == parent.phoneNumber) Color.Gray else Color.Red,
                            contentColor = Color.White
                        )
                    ) {
                        Text(text = "Set as Emergency Contact")
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }


        Text(text = "")
        Spacer(modifier = Modifier.height(8.dp))


        Button(onClick = {
            navController.navigate(Login.route)
            preferenceHelper.clearPreferences()
        },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Red,
                contentColor = Color.White
            ),
            modifier = Modifier.width(240.dp)
        ) {
            androidx.compose.material3.Text(text = "Log out")
        }

    }
}