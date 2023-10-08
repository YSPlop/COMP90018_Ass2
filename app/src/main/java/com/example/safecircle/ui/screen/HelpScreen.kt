package com.example.safecircle.ui.screen

import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.safecircle.ui.components.AppDrawer
import com.example.safecircle.ui.components.AppTopBar
import com.example.safecircle.ui.theme.CyanSecondary
import com.example.safecircle.utils.PreferenceHelper


@Composable
fun HelpScreen(navController: NavHostController) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)

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


    openDialer(emergencyContactNumber, emergencyContactName)

    val tripleZero = "000"

    openDialer(tripleZero, "POLICE")
}


@Composable
fun openDialer(
    emergencyContactNumber: String?,
    emergencyContactName: String?
    ) {

    // in the below line, we are
    // creating variables for URL
    // Replace this with saving parents phone number



    val phoneNumber = emergencyContactNumber

    // on below line we are creating
    // a variable for a context
    val ctx = LocalContext.current

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
