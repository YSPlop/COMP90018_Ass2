package com.example.safecircle.ui.screen

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.safecircle.ChildSettings
import com.example.safecircle.utils.PreferenceHelper
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import androidx.compose.material3.Slider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.safecircle.R
import com.example.safecircle.database.FamilyDatabase
import com.example.safecircle.database.FamilyLocationDao
import com.example.safecircle.ui.components.map.MapMarkerOverlay
import com.example.safecircle.viewmodel.MapViewModel
import com.example.safecircle.sensors.ForegroundSensorService
import com.example.safecircle.ui.theme.PlaypenSansBold
import com.example.safecircle.ui.theme.YellowPrimary
import com.example.safecircle.utils.GlobalState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.Circle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class MarkerProperties(
    var radius: Float = 100f,
    var name: String = "Marker",
    var icon: Int = 0
)

data class EnhancedMarkerState(
    val markerState: MarkerState,
    var properties: MutableState<MarkerProperties> = mutableStateOf(MarkerProperties())
)

@SuppressLint("MutableCollectionMutableState")
@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ChildMapScreen(navController: NavHostController) {
    val context = LocalContext.current
    val preferenceHelper = PreferenceHelper(context)
    val familyId = PreferenceHelper(context).getFamilyID()
    var familyLocationDao = FamilyLocationDao(familyId!!)
    val username = preferenceHelper.getUsername()
    val role = preferenceHelper.getRole()
    val emergencyContactNumber = preferenceHelper.getEmergencyContact()
    val objectID = preferenceHelper.getObjectId()
    val childLocation = remember { mutableStateOf<LatLng?>(null) }
    val markers = remember { mutableStateOf(mapOf<Int, EnhancedMarkerState>()) }
    val selectedMarkerId = remember { mutableStateOf<Int?>(null) }
    val markerIcons = listOf(R.drawable.poi, R.drawable.home, R.drawable.school, R.drawable.friend, R.drawable.sport)
    val smallIcons: List<Bitmap> = generateSmallIcons(context, markerIcons)
    val colors = listOf(Color.Blue.copy(alpha = 0.3f), Color.Green.copy(alpha = 0.3f), Color.Red.copy(alpha = 0.3f), Color.Cyan.copy(alpha = 0.3f), Color.Magenta.copy(alpha = 0.3f))
    val showDialog = remember { mutableStateOf(false) }

    fun isServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    // start all sensors
//    LaunchedEffect(Unit) {
//        Log.i("Dashboard", "objectId: $objectID")
//        // Start ForegroundSensorService
//        if (!isServiceRunning(ForegroundSensorService::class.java)) {
////            ForegroundSensorService.getInstance()?.setUser(familyID.toString(), username.toString(), Role.PARENT)
//            val serviceIntent = Intent(context, ForegroundSensorService::class.java)
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                context.startForegroundService(serviceIntent)
//            } else {
//                context.startService(serviceIntent)
//            }
//        }
//    }

    fun startForegroundService(context: Context) {
        val serviceIntent = Intent(context, ForegroundSensorService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }
    }
    LaunchedEffect(Unit) {
        // Start ForegroundSensorService
        Log.i("Dashboard", "objectId: $objectID")
        val lastObjectID = preferenceHelper.getLastObjectID()

        if (!isServiceRunning(ForegroundSensorService::class.java)) {
            // If service isn't running, start it
            startForegroundService(context)
        } else if (objectID != lastObjectID) {
            // If service is running and objectId has changed, stop and then start it
            val stopIntent = Intent(context, ForegroundSensorService::class.java)
            context.stopService(stopIntent)
            startForegroundService(context)
        }

        // Store the current objectId as lastObjectID
        preferenceHelper.setLastObjectID(objectID.toString())
    }

    val viewModel = viewModel<MapViewModel>(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T: ViewModel> create(modelClass: Class<T>): T {
                return MapViewModel(familyId) as T
            }
        }
    )

    val cameraPositionState = rememberCameraPositionState();
    SideEffect {
        // initial fetching
        viewModel.fetchMemberLocationsAsync {
            var locations = it.values.toMutableList()
//                markers.value.forEach {
//                    locations.add(it.value.markerState.position)
//                }
            Log.d("MapMarkerOverlay", "Calling from LaunchedEffect init it=$it")
            if (username != null) {
                locations = viewModel.memberLocations
                    .filter { it.key == username }
                    .values
                    .toMutableList()
            }
            val update = computeCameraUpdate(locations)
            if (update != null) {
                Log.d("MapMarkerOverlay", "Update Camera init")
                cameraPositionState.move(update)
            }
        }

    }

    LaunchedEffect(GlobalState.markers) {
        try {
            // Initialize marker status for the child
            familyId?.let { famId ->
                username?.let { objId ->
                    familyLocationDao.getMarkersFromChild(famId, objId) {retrievedMarkers ->
                        if (retrievedMarkers != null) {
                            markers.value = retrievedMarkers
                        }
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("ChildMapScreen", "Permission denied: $e")
        }catch (e: Exception) {
            Log.e("ChildMapScreen", "Error fetching location: $e")
        }
    }

    Column(
    ) {
        Column(
            modifier = Modifier.background(Color.Yellow)

        ) {
            TopAppBar(
                title = { androidx.compose.material3.Text(username.toString(), fontFamily = PlaypenSansBold)},
                navigationIcon = {
                    IconButton(onClick = {navController.navigate(ChildSettings.route)}) {
                        Icon(imageVector = Icons.Default.Face, contentDescription = "Child Navigation", Modifier.size(36.dp))
                    }
                },
            )
        }
        Box(
            modifier = Modifier.fillMaxSize()
        ){
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,

                // Deselect the marker when the map is clicked
                onMapClick = { _ ->
                    selectedMarkerId.value = null
                },
            ) {
                markers.value.forEach { entry ->
                    val markerId = entry.key
                    val enhancedMarkerState = entry.value
                    Marker(
                        state = enhancedMarkerState.markerState,
                        title = enhancedMarkerState.properties.value.name,
                        onClick = {
                            selectedMarkerId.value = markerId
                            false
                        },
                        icon = BitmapDescriptorFactory.fromBitmap(smallIcons[enhancedMarkerState.properties.value.icon]),
                    ){}
                    Circle(
                        center = enhancedMarkerState.markerState.position,
                        radius = enhancedMarkerState.properties.value.radius.toDouble(),
                        strokeWidth = 4F,
                        fillColor = colors[enhancedMarkerState.properties.value.icon]
                    )
                }
                MapMarkerOverlay(viewModel = viewModel, username = username)

            }
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
//                    .fillMaxWidth()
                    .padding(8.dp)
                    .verticalScroll(rememberScrollState())
                    .align(Alignment.BottomStart)  // This positions the Column at the bottom
            ) {
                helpPage()
            }
        }
    }
    if (showDialog.value) {
        androidx.compose.material.AlertDialog(
            onDismissRequest = {
                showDialog.value = false
            },
            title = {
                Text(text = "Location Alert")
            },
            text = {
                Text("Current location is inside the circle!")
            },
            confirmButton = {
                Button(onClick = {
                    showDialog.value = false
                }) {
                    Text("OK")
                }
            }
        )
    }
}
fun isLocationInsideCircle(location: LatLng, circleCenter: LatLng, radius: Float): Boolean {
    val earthRadius = 6371e3 // Earth's radius in meters

    val dLat = Math.toRadians(circleCenter.latitude - location.latitude)
    val dLon = Math.toRadians(circleCenter.longitude - location.longitude)

    val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(location.latitude)) * cos(Math.toRadians(circleCenter.latitude)) *
            sin(dLon / 2) * sin(dLon / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))

    val distance = earthRadius * c // Distance in meters

    return distance <= radius
}
private fun computeCameraUpdate(markers: Iterable<LatLng>): CameraUpdate? {
    val markersList = markers.toList()
    if (markersList.isEmpty()) {
        return null
    }
    if (markersList.size == 1) {
        return CameraUpdateFactory.newLatLngZoom(markersList[0], 15f)
    }
    val builder = LatLngBounds.builder()
    markersList.forEach {
        builder.include(it)
    }
    val bounds = builder.build()
    return CameraUpdateFactory.newLatLngBounds(bounds, 500, 1000, 4)
}

@Composable
fun helpPage(

){
    val context = LocalContext.current
    val preferenceHelper = PreferenceHelper(context)
    val emergencyContactNumber = preferenceHelper.getEmergencyContact()
    val emergencyContactName = preferenceHelper.getUsername()

    val tripleZero = "000"
    val textMessageToSend = "Your child has clicked the emergency text button, please check on him/her"
    val number = emergencyContactNumber.toString()

    Row (
        horizontalArrangement = Arrangement.Start,
        modifier = Modifier.padding(start = 0.dp)  // Add left padding to the Row
    ){
        openDialer(tripleZero, "POLICE", context)
        messageButton(
            emergencyContactNumber = number,
            textMessageToSend = textMessageToSend,
            context = context
        )
        openDialer(emergencyContactNumber, "PARENT", context)

    }

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
//        modifier = Modifier
////            .fillMaxSize()
////            .fillMaxHeight()
////            .fillMaxWidth()
//            // on below line we are
//            // adding padding for our column
//            .padding(5.dp),
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
//                    modifier = Modifier.padding(4.dp),
                    color = Color.White,
                    fontFamily = PlaypenSansBold
                )
            }
        }
    }
}

// Message button and call button
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
                Toast.makeText(context, "Emergency message has been sent to parent", Toast.LENGTH_SHORT).show()
            }

        },
        colors = ButtonDefaults.buttonColors(
            containerColor = YellowPrimary,
            contentColor = Color.White
        ),
        modifier = Modifier
            .padding(horizontal = 4.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Send, // Replace with your desired icon
            contentDescription = "Send", // Accessibility description for the icon
            tint = Color.Black
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


    }catch(e: java.lang.Exception){
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
