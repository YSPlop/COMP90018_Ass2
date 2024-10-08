package com.example.safecircle.ui.screen

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.safecircle.Login
import com.example.safecircle.Settings
import com.example.safecircle.database.Child
import com.example.safecircle.database.FamilyDatabase
import com.example.safecircle.database.Parent
import com.example.safecircle.database.PersonalDetails
import com.example.safecircle.sensors.ForegroundSensorService
import com.example.safecircle.ui.components.AppDrawer
import com.example.safecircle.ui.components.AppTopBar
import com.example.safecircle.ui.theme.CyanSecondary
import com.example.safecircle.ui.theme.PlaypenSansBold
import com.example.safecircle.ui.theme.YellowPrimary
import com.example.safecircle.utils.PreferenceHelper
@Composable
fun SettingsScreen(navController: NavHostController) {
    val context = LocalContext.current
    val preferenceHelper = PreferenceHelper(context)
    val familyDatabase = FamilyDatabase()

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val familyId = preferenceHelper.getFamilyID()
    val objectId = preferenceHelper.getObjectId()

    val isParentAccount = remember { mutableStateOf(true) } // to toggle between Parent & Child
    val childName = remember { mutableStateOf(TextFieldValue("")) }
    val childCode = remember { mutableStateOf(TextFieldValue("")) }
    val parentUsername = remember { mutableStateOf(TextFieldValue("")) }
    val parentPassword = remember { mutableStateOf(TextFieldValue("")) }
    val personalDetails = remember { mutableStateOf(PersonalDetails()) }

    // Fetch personal details when the composable loads
    LaunchedEffect(key1 = objectId) {
        familyId?.let { famId ->
            objectId?.let { objId ->
                familyDatabase.getPersonalDetails(famId, objId) { details ->
                    details?.let {
                        personalDetails.value = it
                    }
                }
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = { AppDrawer(drawerState, navController) }
    ) {
        Column(modifier = Modifier.fillMaxSize().background(color = CyanSecondary)) {
            AppTopBar(drawerState, "Settings")

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {

                // Personal Details
                ElevatedCard(
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 6.dp
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(text = "Personal Details", fontWeight = FontWeight.Bold, fontSize = 18.sp, fontFamily = PlaypenSansBold)
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = personalDetails.value.firstName,
                            onValueChange = { personalDetails.value = personalDetails.value.copy(firstName = it) },
                            /*value = firstName.value,
                            onValueChange = { firstName.value = it },

                             */
                            label = { Text("First Name") }
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = personalDetails.value.lastName,
                            onValueChange = { personalDetails.value = personalDetails.value.copy(lastName = it) },
                            label = { Text("Last Name") }
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = personalDetails.value.phoneNumber,
                            onValueChange = { personalDetails.value = personalDetails.value.copy(phoneNumber = it) },
                            label = { Text("Phone Number") }
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = personalDetails.value.address,
                            onValueChange = { personalDetails.value = personalDetails.value.copy(address = it) },
                            label = { Text("Address") }
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                familyId?.let { famId ->
                                    objectId?.let { objId ->
                                        familyDatabase.savePersonalDetails(famId, objId, personalDetails.value) { success, message ->
                                            if (success) {
                                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(context, "Failed to save details: $message", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = YellowPrimary,
                                contentColor = Color.White
                            )
                        ) {
                            Text(text = "Save Details", fontFamily = PlaypenSansBold)
                        }
                    }
                }

                // Account Creation
                ElevatedCard(
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 6.dp
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            text = if (isParentAccount.value) "Parent Account Creation" else "Child Account Creation",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            fontFamily = PlaypenSansBold
                        )
                    }
                        Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { isParentAccount.value = true },
                            enabled = !isParentAccount.value,
                            colors = ButtonDefaults.buttonColors(
                                disabledContainerColor = CyanSecondary,
                                containerColor = Color.LightGray,
                                contentColor = Color.Gray,
                                disabledContentColor = Color.White
                            )
                        ) {
                            Text(text = "Parent", fontFamily = PlaypenSansBold)
                        }

                        Button(
                            onClick = { isParentAccount.value = false },
                            enabled = isParentAccount.value,
                            colors = ButtonDefaults.buttonColors(
                                disabledContainerColor = CyanSecondary,
                                containerColor = Color.LightGray,
                                contentColor = Color.Gray,
                                disabledContentColor = Color.White
                            )
                        ) {
                            Text(text = "Child", fontFamily = PlaypenSansBold)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (isParentAccount.value) {

                        OutlinedTextField(
                            value = parentUsername.value,
                            onValueChange = { parentUsername.value = it },
                            label = { Text("Username") }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = parentPassword.value,
                            onValueChange = { parentPassword.value = it },
                            label = { Text("Password") }
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Logic to create parent account goes here
                        Button(
                            onClick = {
                                familyId?.let { famId ->
                                    // Assuming you have fields called parentUsername and parentPassword for input
                                    val newParent =
                                        Parent(parentUsername.value.text, parentPassword.value.text)

                                    familyDatabase.addNewParentToFamily(famId, newParent,
                                        onSuccess = {
                                            Toast.makeText(
                                                context,
                                                "Parent account ${parentUsername.value.text} has been created",
                                                Toast.LENGTH_SHORT
                                            ).show()

                                            // Clear the textfields
                                            parentUsername.value = TextFieldValue("")
                                            parentPassword.value = TextFieldValue("")
                                        },
                                        onFailure = { errorMsg ->
                                            Toast.makeText(
                                                context,
                                                errorMsg,
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    )
                                }
                            }, colors = ButtonDefaults.buttonColors(
                                containerColor = YellowPrimary,
                                contentColor = Color.White
                            )
                        )
                        {
                            Text(text = "Create Account", fontFamily = PlaypenSansBold)
                        }

                    } else {
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

                        Button(
                            onClick = {
                                familyId?.let {
                                    val newChild = Child(childName.value.text, childCode.value.text)
                                    familyDatabase.addChildToFamily(it, newChild)
                                    Toast.makeText(
                                        context,
                                        "Account ${childName.value.text} has been created",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    childCode.value = TextFieldValue("")
                                    childName.value = TextFieldValue("")
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = YellowPrimary,
                                contentColor = Color.White
                            )
                        ) {
                            Text(text = "Create Account", fontFamily = PlaypenSansBold)
                        }
                    }
                }}

                Spacer(modifier = Modifier.height(8.dp))

                Button(onClick = {
                    navController.navigate(Login.route){
                        popUpTo(navController.graph.startDestinationRoute!!) { inclusive = true }
                    }
                    preferenceHelper.clearPreferences()
                },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.width(240.dp)
                ) {
                    Text(text = "Log out", fontFamily = PlaypenSansBold)
                }
            }
        }
    }
}
