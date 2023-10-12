package com.example.safecircle.ui.screen

import android.app.Activity
import android.app.PendingIntent
import android.app.PendingIntent.getBroadcast
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.telephony.SmsManager
import android.telephony.SubscriptionManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.getSystemService
import androidx.navigation.NavHostController
import com.example.safecircle.ui.components.AppDrawer
import com.example.safecircle.ui.components.AppTopBar
import com.example.safecircle.ui.theme.CyanSecondary
import com.example.safecircle.ui.theme.YellowPrimary
import com.example.safecircle.utils.PreferenceHelper
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.lang.Exception


@Composable
fun HelpScreen(navController: NavHostController) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)





//    val requestSendSMSPermissionLauncher = rememberUpdatedState(
//        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
//            if (it){
//                smsSensorManager = SmsManager(this){
//
//                }
//            }
//        }
//    ).value
//
//    if (ContextCompat.checkSelfPermission(
//            context,
//            android.Manifest.permission.SEND_SMS
//        ) != PackageManager.PERMISSION_GRANTED
//    ) {
//        requestSendSMSPermissionLauncher.launch(android.Manifest.permission.SEND_SMS)
//    } else {
//        Toast.makeText(
//            context,
//            "Please give SMS permissions to the app",
//            Toast.LENGTH_LONG
//        ).show()
//        requestSendSMSPermissionLauncher.launch(android.Manifest.permission.SEND_SMS)
//    }


    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = { AppDrawer(drawerState, navController) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = CyanSecondary)
        ) {
            AppTopBar(drawerState, "Help")
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

    val textMessageToSend = "Your child has clicked the emergency text button, please check on them"

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
                Text(
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
        Text(
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
