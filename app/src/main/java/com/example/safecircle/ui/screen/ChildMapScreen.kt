package com.example.safecircle.ui.screen

import android.Manifest
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.Button
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
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
import androidx.compose.ui.draw.clip
import com.google.android.gms.location.LocationServices
import com.google.maps.android.compose.Circle
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@SuppressLint("MutableCollectionMutableState")
@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ChildMapScreen(navController: NavHostController) {
    val locationPermissions = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )
    LaunchedEffect(Unit) {
        locationPermissions.launchMultiplePermissionRequest()
    }

    val uniMelbCoord = LatLng(-37.798919,144.964232)
    val circleRadius = remember { mutableStateOf(1000f) }
    val context = LocalContext.current
    val preferenceHelper = PreferenceHelper(context)
    val familyID = preferenceHelper.getFamilyID()
    val username = preferenceHelper.getUsername()
    val objectID = preferenceHelper.getObjectId()
    val role = preferenceHelper.getRole()
    val emergencyContactNumber = preferenceHelper.getEmergencyContact()
    val childLocation = remember { mutableStateOf<LatLng?>(null) }
    val markers = remember { mutableStateOf(listOf(MarkerState(position = uniMelbCoord))) }
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    val currentLocation = remember { mutableStateOf<LatLng?>(null) }
    val currentLocationMarker = remember { mutableStateOf<MarkerState?>(null) }
    val cameraPositionState = rememberCameraPositionState()
    val selectedMarker = remember { mutableStateOf<MarkerState?>(null) }
    val markerRadiusMap = remember { mutableStateOf(mutableMapOf<MarkerState, Float>()) }
    val markerNameMap = remember { mutableStateOf(mutableMapOf<MarkerState, String>()) }
    val showDialog = remember { mutableStateOf(false) }
    val wasInsideCircle = remember { mutableStateOf(false) }



    if (locationPermissions.allPermissionsGranted) {
        LaunchedEffect(Unit) {
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    currentLocation.value = LatLng(location.latitude, location.longitude)
                    currentLocationMarker.value = MarkerState(position = currentLocation.value!!)
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(currentLocation.value!!, 10f)
                }
            } catch (e: SecurityException) {
                Log.e("ChildMapScreen", "Permission denied: $e")
            }catch (e: Exception) {
                Log.e("ChildMapScreen", "Error fetching location: $e")
            }
        }
    }
    Column(
    ) {
        Column(
            modifier = Modifier.background(Color.Yellow)

        ) {
            TopAppBar(
                title = { androidx.compose.material3.Text(username.toString())},
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
                onMapClick = { _ ->
                    // Deselect the marker when the map is clicked
                    selectedMarker.value = null
                },
                onMapLongClick = { latLng -> // 2. Update onMapClick
                    Log.d("ChildMapScreen", "Map clicked at: $latLng")
                    // Add the clicked location as a new marker to the list
                    val newMarker = MarkerState(position = latLng)
                    markers.value = markers.value + newMarker
                }
            ) {
                markers.value.forEach { markerState ->
                    currentLocation.value?.let { _ ->
                        val isInside = isLocationInsideCircle(
                            currentLocation.value!!,
                            markerState.position,
                            markerRadiusMap.value.getOrDefault(markerState, 100f)
                        )
                        if (isInside && !wasInsideCircle.value) {
                            Log.d("ChildMapScreen", "Current location is inside the circle for marker: $markerState")
//                            showDialog.value = true
//                            wasInsideCircle.value = true
                        }
//                        else if(!isInside){
//                            wasInsideCircle.value = false
//                        }
                    }
                    Marker(
                        state = markerState,
//                        title = "Custom Marker",
//                        snippet = "Marker at $markerState.position",
                        onClick = {
                            selectedMarker.value = markerState
                            if (markerState !in markerRadiusMap.value) {
                                val updatedMap = markerRadiusMap.value.toMutableMap()
                                updatedMap[markerState] = 100f
                                markerRadiusMap.value = updatedMap
                            }
                            if (markerState !in markerNameMap.value) {
                                val updatedNameMap = markerNameMap.value.toMutableMap()
                                updatedNameMap[markerState] = "Marker"
                                markerNameMap.value = updatedNameMap
                            }
                            true
                        }
                    ){}
                    if (selectedMarker.value == markerState) {
                        Circle(
                            center = markerState.position,
                            radius = markerRadiusMap.value.getOrDefault(markerState, 100f).toDouble(),
                            fillColor = Color.Blue.copy(alpha = 0.3f)
                        )
                    } else {
                        Circle(
                            center = markerState.position,
                            radius = markerRadiusMap.value.getOrDefault(markerState, 100f).toDouble(),
                            fillColor = Color.Blue.copy(alpha = 0.3f)
                        )
                    }
                }
                currentLocationMarker.value?.let { markerState ->
                    Marker(
                        state = markerState,
                        title = "My Location",
                        snippet = "Current device location"
                    )
                }
            }
            if (selectedMarker.value != null) {
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                        .background(MaterialTheme.colors.surface.copy(alpha = 0.8f))
                        .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                ){
                    TextField(
                        value = markerNameMap.value[selectedMarker.value] ?: "",
                        onValueChange = {
                            val updatedNameMap = markerNameMap.value.toMutableMap()
                            updatedNameMap[selectedMarker.value!!] = it
                            markerNameMap.value = updatedNameMap
                        },
                        label = { Text("Marker Name") },
                        modifier = Modifier.padding(8.dp).fillMaxWidth()
                    )

                    Slider(
                        value = markerRadiusMap.value[selectedMarker.value] ?: 100f,
                        onValueChange = { newValue ->
                            val updatedMap = markerRadiusMap.value.toMutableMap()
                            updatedMap[selectedMarker.value!!] = newValue
                            markerRadiusMap.value = updatedMap
                        },
                        valueRange = 30f..500f,
                        modifier = Modifier.padding(all = 16.dp)
                    )
                    Text(text = "${(markerRadiusMap.value[selectedMarker.value] ?: 100f).toInt()} meters",)

                    Button(
                        onClick = {
                            markers.value = markers.value.filter { it != selectedMarker.value }
                            selectedMarker.value = null
                        },
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text(text = "Remove Marker")
                    }
                }
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