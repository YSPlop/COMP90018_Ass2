package com.example.safecircle.ui.screen

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.safecircle.ui.components.AppDrawer
import com.example.safecircle.ui.components.AppTopBar


@Composable
fun HelpScreen(navController: NavHostController) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = { AppDrawer(drawerState, navController) }
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
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
                openDialer()
            }
        }
    }
}


@Composable
fun openDialer() {

    // in the below line, we are
    // creating variables for URL
    // Replace this with saving parents phone number
    val phoneNumber = remember {
        mutableStateOf(TextFieldValue())
    }

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

        // in the below line, we are creating a
        // text field for getting URL from user.
        TextField(
            // on below line we are specifying value
            // for our  text field.
            value = phoneNumber.value,

            // on below line we are adding on value
            // change for text field.
            onValueChange = { phoneNumber.value = it },

            // on below line we are adding place holder as text
            placeholder = { Text(text = "Enter your phone number") },

            // on below line we are adding modifier to it
            // and adding padding to it and filling max width
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),

            // on below line we are adding text style
            // specifying color and font size to it.
            textStyle = TextStyle(color = Color.Black, fontSize = 15.sp),

            // on below line we are adding single line to it.
            singleLine = true,
        )

        // on below line adding a spacer.
        Spacer(modifier = Modifier.height(20.dp))

        // on below line adding a button to open URL
        Button(onClick = {
            // on below line we are opening the dialer of our
            // phone and passing phone number.
            // Use format with "tel:" and phoneNumber created is
            // stored in u.
            val u = Uri.parse("tel:" + phoneNumber.value.text)

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


        }) {
            // on below line creating a text for our button.
            Text(
                // on below line adding a text ,
                // padding, color and font size.
                text = "Dial",
                modifier = Modifier.padding(10.dp),
                color = Color.White,
                fontSize = 15.sp
            )
        }
    }
}
