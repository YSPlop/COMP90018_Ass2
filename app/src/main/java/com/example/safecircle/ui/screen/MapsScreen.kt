package com.example.safecircle.ui.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.safecircle.database.FamilyDatabase
import com.example.safecircle.database.FamilyLocationDao
import com.example.safecircle.ui.components.map.MapMarkerOverlay
import com.example.safecircle.utils.ErrorDialog
import com.example.safecircle.utils.PreferenceHelper
import com.example.safecircle.viewmodel.MapViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState


@Composable
fun MapsScreen(navController: NavController, username: String? = null) {
    Log.d("MapScreen", "Received username = $username")
    val context = LocalContext.current;
    val familyId = PreferenceHelper(context).getFamilyID();
    var _familyLocationDao = FamilyLocationDao.getInstance(familyId!!)
    val markers = remember { mutableStateOf(mutableMapOf<Int, EnhancedMarkerState>()) }
    val lastKnownMarkers = remember { mutableStateOf(mutableMapOf<Int, EnhancedMarkerState>()) }
    val selectedMarkerId = remember { mutableStateOf<Int?>(null) }
    val showDialog = remember { mutableStateOf(false) }

    // Function to check if the markers has changed
    fun hasMarkersChanged(): Boolean {
        return markers.value != lastKnownMarkers.value
    }

    // Update the last-known marker value
    fun updateLastKnownMarkers() {
        lastKnownMarkers.value = markers.value.toMutableMap()
    }

    // Call this function after you have checked, to update the last-known value
    fun restoreMarkers() {
        markers.value = lastKnownMarkers.value.toMutableMap()
    }
//    val familyId = "testFamily"
    if (familyId.isNullOrBlank()) {
        ErrorDialog(message = "FamilyId not set") {}
        return
    }

    val viewModel = viewModel<MapViewModel>(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T: ViewModel> create(modelClass: Class<T>): T {
                return MapViewModel(familyId) as T
            }
        }
    )

    val cameraPositionState = viewModel.cameraState

    LaunchedEffect(Unit) {
        // Initialize marker status for the child
        familyId?.let { famId ->
            username?.let { objId ->
                _familyLocationDao.getMarkersFromChild(famId, objId) {retrievedMarkers ->
                    if(retrievedMarkers != null){
                        markers.value = retrievedMarkers
                        // Update the last marker state to current
                        updateLastKnownMarkers()
                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ){
        GoogleMap(
            modifier = Modifier
                .safeContentPadding()
                .fillMaxSize(),
            cameraPositionState = cameraPositionState,

            // Deselect the marker when the map is clicked
            onMapClick = { _ ->
                selectedMarkerId.value = null
                if(hasMarkersChanged()){
                    restoreMarkers()
                }
            },
            // Place down new marker
            onMapLongClick = { latLng ->
                Log.d("ChildMapScreen", "Map clicked at: $latLng")
                // Add the clicked location as a new marker to the list
                val newMarker = EnhancedMarkerState(MarkerState(position = latLng))
                // Find the largest ID in the map and increment it by 1
                val markerId = if (markers.value.isEmpty()) {
                    1
                } else {
                    markers.value.keys.maxOrNull()!! + 1
                }

                val updatedMarkers = markers.value.toMutableMap()
                updatedMarkers[markerId] = newMarker
                markers.value = updatedMarkers

                familyId?.let { famId ->
                    username?.let { objId ->
                        _familyLocationDao.pushMarkersToChild(famId, objId, markers.value)
                    }
                }
                // Update the last marker state to current
                updateLastKnownMarkers()
            }

        ) {
            markers.value.forEach { entry ->
                val markerId = entry.key
                val enhancedMarkerState = entry.value
                Marker(
                    state = enhancedMarkerState.markerState,
                    onClick = {
                        selectedMarkerId.value = markerId
                        true
                    }
                ){}
                Circle(
                    center = enhancedMarkerState.markerState.position,
                    radius = enhancedMarkerState.properties.value.radius.toDouble(),
                    fillColor = Color.Blue.copy(alpha = 0.3f)
                )
            }
            MapMarkerOverlay(viewModel = viewModel, username = username)
        }
        // Access the selected marker using its ID
        val selectedEnhancedMarker = selectedMarkerId.value?.let { markers.value[it] }
        if (selectedEnhancedMarker != null) {
            // Find the EnhancedMarkerState corresponding to the selected MarkerState
            val selectedName = selectedEnhancedMarker?.properties?.value?.name ?: ""
            val selectedRadius = selectedEnhancedMarker?.properties?.value?.radius ?: 100f
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp)
                    .background(MaterialTheme.colors.surface.copy(alpha = 0.8f))
                    .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
            ){
                TextField(
                    value = selectedName,
                    onValueChange = { newName ->
                        val updatedMarker = selectedEnhancedMarker?.copy(
                            properties = mutableStateOf(
                                selectedEnhancedMarker.properties.value.copy(name = newName)
                            )
                        )
                        if (updatedMarker != null && selectedMarkerId.value != null) {
                            markers.value = markers.value.toMutableMap().apply {
                                this[selectedMarkerId.value!!] = updatedMarker
                            }
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
                        val updatedMarker = selectedEnhancedMarker?.copy(
                            properties = mutableStateOf(
                                selectedEnhancedMarker.properties.value.copy(radius = newValue)
                            )
                        )
                        if (updatedMarker != null && selectedMarkerId.value != null) {
                            markers.value = markers.value.toMutableMap().apply {
                                this[selectedMarkerId.value!!] = updatedMarker
                            }
                        }
                    },
                    valueRange = 30f..500f,
                    modifier = Modifier.padding(all = 16.dp)
                )
                Text(text = "${(selectedEnhancedMarker?.properties?.value?.radius ?: 100f).toInt()} meters",)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ){
                    Button(
                        // Delete marker for this child
                        onClick = {
                            markers.value.remove(selectedMarkerId.value)
                            familyId?.let { famId ->
                                username?.let { objId ->
                                    _familyLocationDao.pushMarkersToChild(famId, objId, markers.value)
                                }
                            }
                            // Update the last marker state to current
                            updateLastKnownMarkers()
                            selectedMarkerId.value = null
                        },
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text(text = "Remove Marker")
                    }
                    Button(
                        // Save button activated when changes are uncommitted to firebase
                        enabled = hasMarkersChanged(),
                        // Save changes for markers
                        onClick = {
                            familyId?.let { famId ->
                                username?.let { objId ->
                                    _familyLocationDao.pushMarkersToChild(famId, objId, markers.value)
                                }
                            }
                            // Update the last marker state to current
                            updateLastKnownMarkers()
                        },
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text(text = "Save")
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


