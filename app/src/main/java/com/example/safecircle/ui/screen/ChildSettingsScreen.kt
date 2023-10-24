package com.example.safecircle.ui.screen

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
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
import androidx.compose.material3.ElevatedButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.example.safecircle.ChildMap
import com.example.safecircle.Login
import com.example.safecircle.database.FamilyDatabase
import com.example.safecircle.database.PersonalDetails
import com.example.safecircle.sensors.ForegroundSensorService
import com.example.safecircle.ui.theme.CyanSecondary
import com.example.safecircle.ui.theme.PlaypenSansBold
import com.example.safecircle.ui.theme.YellowPrimary
import java.lang.Exception

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
            title = { androidx.compose.material3.Text(username.toString(), fontFamily = PlaypenSansBold) },
            navigationIcon = {
                IconButton(onClick = {navController.navigate(ChildMap.route)}) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Child Navigation", Modifier.size(36.dp))
                }
            }
        )
        Divider(modifier = Modifier.padding(8.dp))
        Row {
            Text(text = "Emergency Contact Number: ", fontFamily = PlaypenSansBold)
            Text(text = if (emergencyContactNumber != null) emergencyContactNumber.toString() else "NOT SET YET", fontFamily = PlaypenSansBold)
        }
        // Display parents' details as cards
        parentsList.forEach { parent ->
            Card(modifier = Modifier.padding(16.dp)) {
                Column (modifier = Modifier.padding(36.dp)) {
                    Text(text = parent.firstName, fontFamily = PlaypenSansBold, fontSize = 24.sp)
                    Text(text = parent.lastName, fontFamily = PlaypenSansBold)
                    Text(text = parent.address, fontFamily = PlaypenSansBold)
                    Text(text = parent.phoneNumber, fontFamily = PlaypenSansBold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            // Set the person's phone number as emergency contact
                            setSelectedEmergencyContact(parent.phoneNumber)
                            preferenceHelper.setEmergencyContact(parent.phoneNumber)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedEmergencyContact == parent.phoneNumber) Color.Gray else YellowPrimary,
                            contentColor = Color.White
                        )
                    ) {
                        Text(text = "Set as Emergency Contact", fontFamily = PlaypenSansBold)
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
            androidx.compose.material3.Text(text = "Log out", fontFamily = PlaypenSansBold)
        }

    }

}