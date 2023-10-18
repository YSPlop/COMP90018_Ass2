package com.example.safecircle.ui.screen

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
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

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Have a hyperlink to how to keep your children safe
            // Have a green call button to parents
            // Do we need more?
            helpPage()
        }

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


@Composable
fun helpPage(

){

    val context = LocalContext.current
    val preferenceHelper = PreferenceHelper(context)
    val emergencyContactNumber = preferenceHelper.getEmergencyContact()
    val emergencyContactName = preferenceHelper.getUsername()


    openDialer(emergencyContactNumber, emergencyContactName, context)

    val tripleZero = "000"

    openDialer(tripleZero, "POLICE", context)

    val textMessageToSend = "Your child has clicked the emergency text button, please check on him/her"

    val number = emergencyContactNumber.toString()

    messageButton(
        emergencyContactNumber = number,
        textMessageToSend = textMessageToSend,
        context = context
    )
}


@Composable
fun openDialer(
    emergencyContactNumber: String?,
    emergencyContactName: String?,
    context: Context,
) {

    // in the below line, we are
    // creating variables for URL
    // Replace this with saving parents phone number



    val phoneNumber = emergencyContactNumber

    // on below line we are creating
    // a variable for a context
    val ctx = context

    // on below line we are creating a column
    Column(
        // on below line we are specifying modifier
        // and setting max height and max width
        // for our column
        modifier = Modifier
            .fillMaxSize()
            .fillMaxHeight()
            .fillMaxWidth()
            // on below line we are
            // adding padding for our column
            .padding(5.dp),
        // on below line we are specifying horizontal
        // and vertical alignment for our column
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // on below line adding a button to open URL
        ElevatedButton(onClick = {
            // on below line we are opening the dialer of our
            // phone and passing phone number.
            // Use format with "tel:" and phoneNumber created is
            // stored in u.
            val u = Uri.parse("tel:$phoneNumber")

            // Create the intent and set the data for the
            // intent as the phone number.
            val i = Intent(Intent.ACTION_DIAL, u)
            try {

                // Launch the Phone app's dialer with a phone
                // number to dial a call.
                ctx.startActivity(i)
            } catch (s: SecurityException) {

                // show() method display the toast with
                // exception message.
                Toast.makeText(ctx, "An error occurred", Toast.LENGTH_LONG)
                    .show()
            }


        },
            colors = ButtonDefaults.elevatedButtonColors(Color.Red)
        ) {
            // on below line creating a text for our button.
            if (emergencyContactName != null) {
                androidx.compose.material3.Text(
                    // on below line adding a text ,
                    // padding, color and font size.
                    text = emergencyContactName,
                    modifier = Modifier.padding(10.dp),
                    color = Color.White,
                    fontSize = 15.sp
                )
            }
        }
    }
}

@Composable
fun messageButton(
    emergencyContactNumber: String,
    textMessageToSend: String,
    context: Context,
){
    ElevatedButton(
        onClick = {
            val activity = context as Activity

            val result = requestSmsPermission(activity, 123)

            if (result) {
                Log.i("SMS", "permissions granted")
            }else{
                Log.i("SMS", "no permissions ")
                requestSmsPermission(activity, 123)
            }

//            startSMSRetrieverClient(context)

            val newResult = requestSmsPermission(activity, 123)
            if (newResult) {
                Log.i("SMS", "permissions are working")
                sendTextMessage1(context, emergencyContactNumber, textMessageToSend, activity)
            }

        },
        colors = ButtonDefaults.buttonColors(
            containerColor = YellowPrimary,
            contentColor = Color.White
        ),
        modifier = Modifier
            .padding(horizontal = 15.dp)
    ) {
        androidx.compose.material3.Text(
            text = "click to send text",
            color = Color.Black
        )
    }

}

fun sendTextMessage1(context: Context, phoneNumber: String, message: String, activity: Activity) {


    try{
        // on below line initializing sms manager.
        Log.i("SMS","I am about to start the manager")
        val smsManager: SmsManager = SmsManager.getDefault()
        Log.i("SMS","I am about to send the message")
        smsManager.sendTextMessage(phoneNumber, null, message, null, null, 1)


    }catch(e: Exception){
        // on below line handling error and
        // displaying toast message.
        Toast.makeText(
            context,
            "Error : " + e.message,
            Toast.LENGTH_SHORT
        ).show()

        e.message?.let { Log.d("SMS", it)
        }
    }

}
fun requestSmsPermission(activity: Activity, requestCode: Int): Boolean {

    if (activity.checkSelfPermission(android.Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
        return true
    } else {
        activity.requestPermissions(arrayOf(android.Manifest.permission.SEND_SMS), requestCode)
        return false
    }
}
