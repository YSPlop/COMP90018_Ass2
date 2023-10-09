package com.example.safecircle.ui.screen

import android.Manifest
import android.adservices.adid.AdId
import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.example.safecircle.database.FamilyDatabase
import com.google.android.gms.location.LocationServices
import com.google.maps.android.compose.Circle
import java.util.UUID
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class MarkerProperties(
    var radius: Float = 100f,
    var name: String = "Marker"
)

data class EnhancedMarkerState(
    val markerState: MarkerState,
    var properties: MutableState<MarkerProperties> = mutableStateOf(MarkerProperties())
)

@SuppressLint("MutableCollectionMutableState")
@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ChildMapScreen(navController: NavHostController) {
    val familyDatabase = FamilyDatabase()
    val uniMelbCoord = LatLng(-37.798919,144.964232)
    val context = LocalContext.current
    val preferenceHelper = PreferenceHelper(context)
    val familyID = preferenceHelper.getFamilyID()
    val username = preferenceHelper.getUsername()
    val objectID = preferenceHelper.getObjectId()
    val role = preferenceHelper.getRole()
    val emergencyContactNumber = preferenceHelper.getEmergencyContact()
    val childLocation = remember { mutableStateOf<LatLng?>(null) }
//    val markers = remember { mutableStateOf(listOf(EnhancedMarkerState(MarkerState(position = uniMelbCoord)))) }
    val markers = remember { mutableStateOf(mutableMapOf<Int, EnhancedMarkerState>()) }
    val selectedMarkerId = remember { mutableStateOf<Int?>(null) }
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    val currentLocation = remember { mutableStateOf<LatLng?>(null) }
    val currentLocationMarker = remember { mutableStateOf<MarkerState?>(null) }
    val cameraPositionState = rememberCameraPositionState()
    val selectedMarker = remember { mutableStateOf<MarkerState?>(null) }
    val showDialog = remember { mutableStateOf(false) }
    val wasInsideCircle = remember { mutableStateOf(false) }

    val locationPermissions = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )
    LaunchedEffect(Unit) {
        locationPermissions.launchMultiplePermissionRequest()
    }

    if (locationPermissions.allPermissionsGranted) {
        LaunchedEffect(Unit) {
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    currentLocation.value = LatLng(location.latitude, location.longitude)
                    currentLocationMarker.value = MarkerState(position = currentLocation.value!!)
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(currentLocation.value!!, 10f)
                }
                familyID?.let { famId ->
                    objectID?.let { objId ->
                        familyDatabase.getMarkersFromChild(famId, objId, markers)
                    }
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
                    selectedMarkerId.value = null
                },
                onMapLongClick = { latLng -> // 2. Update onMapClick
                    Log.d("ChildMapScreen", "Map clicked at: $latLng")
                    // Add the clicked location as a new marker to the list
                    val newMarker = EnhancedMarkerState(MarkerState(position = latLng))
//                    val markerId = UUID.randomUUID()
                    val markerId = markers.value.size + 1;
                    val updatedMarkers = markers.value.toMutableMap()  // Create a new mutable copy
                    updatedMarkers[markerId] = newMarker
                    markers.value = updatedMarkers

                    familyID?.let { famId ->
                        objectID?.let { objId ->
                            familyDatabase.pushMarkersToChild(famId, objId, markers.value)
                        }
                    }
//                    markers.value = markers.value + newMarker
                }
            ) {
                markers.value.forEach { entry ->
                    val markerId = entry.key
                    val enhancedMarkerState = entry.value
//                    currentLocation.value?.let { _ ->
//                        val isInside = isLocationInsideCircle(
//                            currentLocation.value!!,
//                            enhancedMarkerState.markerState.position,
//                            enhancedMarkerState.properties.value.radius
//                        )
//                        if (isInside && !wasInsideCircle.value) {
//                            Log.d("ChildMapScreen", "Current location is inside the circle for marker: ${enhancedMarkerState.markerState}")
////                            showDialog.value = true
////                            wasInsideCircle.value = true
//                        }
////                        else if(!isInside){
////                            wasInsideCircle.value = false
////                        }
//                    }
                    Marker(
                        state = enhancedMarkerState.markerState,
                        onClick = {
                            selectedMarkerId.value = markerId
//                            selectedMarker.value = enhancedMarkerState.markerState
                            true
                        }
                    ){}
                    Circle(
                        center = enhancedMarkerState.markerState.position,
                        radius = enhancedMarkerState.properties.value.radius.toDouble(),
                        fillColor = Color.Blue.copy(alpha = 0.3f)
                    )
                }
                currentLocationMarker.value?.let { markerState ->
                    Marker(
                        state = markerState,
                        title = "My Location",
                        snippet = "Current device location"
                    )
                }
            }
            // Access the selected marker using its UUID
            val selectedEnhancedMarker = selectedMarkerId.value?.let { markers.value[it] }
            if (selectedEnhancedMarker != null) {
                // Find the EnhancedMarkerState corresponding to the selected MarkerState
//                val selectedEnhancedMarker = markers.value.find { it.markerState == selectedMarker.value }
                val selectedName = selectedEnhancedMarker?.properties?.value?.name ?: ""
                val selectedRadius = selectedEnhancedMarker?.properties?.value?.radius ?: 100f
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                        .background(MaterialTheme.colors.surface.copy(alpha = 0.8f))
                        .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                ){
                    TextField(
                        value = selectedName,
                        onValueChange = { newName ->
                            val currentProperties = selectedEnhancedMarker?.properties?.value
                            if (currentProperties != null) {
                                selectedEnhancedMarker.properties.value = currentProperties.copy(name = newName)
                            }
                        },
                        label = { Text("Marker Name") },
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                    )

                    Slider(
                        value = selectedRadius,
                        onValueChange = { newValue ->
                            val currentProperties = selectedEnhancedMarker?.properties?.value
                            if (currentProperties != null) {
                                selectedEnhancedMarker.properties.value = currentProperties.copy(radius = newValue)
                            }
                        },
                        valueRange = 30f..500f,
                        modifier = Modifier.padding(all = 16.dp)
                    )
                    Text(text = "${(selectedEnhancedMarker?.properties?.value?.radius ?: 100f).toInt()} meters",)

                    Button(
                        onClick = {
                            markers.value.remove(selectedMarkerId.value)
//                            markers.value = markers.value.filter { it != selectedEnhancedMarker }
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