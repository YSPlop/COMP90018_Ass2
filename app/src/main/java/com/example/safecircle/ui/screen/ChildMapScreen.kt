package com.example.safecircle.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.Divider
import androidx.compose.material.Text
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
import com.example.safecircle.ChildSettings
import com.example.safecircle.utils.PreferenceHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChildMapScreen(navController: NavHostController) {
    val context = LocalContext.current
    val preferenceHelper = PreferenceHelper(context)
    val familyID = preferenceHelper.getFamilyID()
    val username = preferenceHelper.getUsername()
    val objectID = preferenceHelper.getObjectId()
    val role = preferenceHelper.getRole()
    val emergencyContactNumber = preferenceHelper.getEmergencyContact()
    Column(

    ) {
        Column {
            TopAppBar(
                title = { androidx.compose.material3.Text(username.toString())},
                navigationIcon = {
                    IconButton(onClick = {navController.navigate(ChildSettings.route)}) {
                        Icon(imageVector = Icons.Default.Face, contentDescription = "Child Navigation", Modifier.size(36.dp))
                    }
                }
            )
            Divider()
            Column {
                Row {
                    Text(text = "emergency contact number: ")
                    Text(text = emergencyContactNumber.toString())
                }
            }

        }

        Text(text = "Child Map")


    }

}