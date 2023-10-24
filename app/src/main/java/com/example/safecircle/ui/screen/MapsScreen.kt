package com.example.safecircle.ui.screen

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.Shapes
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults.filledIconButtonColors
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.Button
//import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCompositionContext
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.safecircle.R
import com.example.safecircle.ChildSettings
import com.example.safecircle.Dashboard
import com.example.safecircle.database.FamilyDatabase
import com.example.safecircle.database.FamilyLocationDao
import com.example.safecircle.ui.components.map.MapMarkerOverlay
import com.example.safecircle.ui.theme.YellowPrimary
import com.example.safecircle.utils.ErrorDialog
import com.example.safecircle.utils.PreferenceHelper
import com.example.safecircle.viewmodel.MapViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout


@Composable
fun MapsScreen(navController: NavController, username: String? = null) {
    Log.d("MapScreen", "Received username = $username")
    val context = LocalContext.current;
    val familyId = PreferenceHelper(context).getFamilyID();
    var familyLocationDao = FamilyLocationDao(familyId!!)
    val markers = remember { mutableStateOf(mapOf<Int, EnhancedMarkerState>()) }
    val lastKnownMarkers = remember { mutableStateOf(mapOf<Int, EnhancedMarkerState>()) }
    val selectedMarkerId = remember { mutableStateOf<Int?>(null) }
    // List of icons
    val poi = painterResource(id = R.drawable.poi)
    val home = painterResource(id = R.drawable.home)
    val school = painterResource(id = R.drawable.school)
    val friend = painterResource(id = R.drawable.friend)
    val sport = painterResource(id = R.drawable.sport)
    val icons = listOf(poi, home, school, friend, sport)
    val markerIcons = listOf(R.drawable.poi, R.drawable.home, R.drawable.school, R.drawable.friend, R.drawable.sport)
    val smallIcons: List<Bitmap> = generateSmallIcons(context, markerIcons)
    val colors = listOf(Color.Blue.copy(alpha = 0.3f), Color.Green.copy(alpha = 0.3f), Color.Red.copy(alpha = 0.3f), Color.Cyan.copy(alpha = 0.3f), Color.Magenta.copy(alpha = 0.3f))
    val showDialog = remember { mutableStateOf(false) }

    val cameraPositionState = rememberCameraPositionState();
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

    val scope = rememberCoroutineScope()
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


    LaunchedEffect(Unit) {
        // Initialize marker status for the child
        familyId?.let { famId ->
            username?.let { objId ->
                familyLocationDao.getMarkersFromChild(famId, objId) {retrievedMarkers ->
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
        Log.d("MapMarkerOverlay", "Google Map rendered")
        GoogleMap(
            modifier = Modifier
                .safeContentPadding()
                .fillMaxSize(),
            cameraPositionState = remember {
                cameraPositionState
            },

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
                        familyLocationDao.pushMarkersToChild(famId, objId, markers.value)
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
                    icon = BitmapDescriptorFactory.fromBitmap(smallIcons[enhancedMarkerState.properties.value.icon]),
                    onClick = {
                        selectedMarkerId.value = markerId
                        true
                    }
                ){}
                Circle(
                    center = enhancedMarkerState.markerState.position,
                    radius = enhancedMarkerState.properties.value.radius.toDouble(),
                    fillColor = colors[enhancedMarkerState.properties.value.icon]
                )
            }
            MapMarkerOverlay(viewModel = viewModel, username = username)
        }
        IconButton(
            onClick = { navController.navigate(Dashboard.route) },
            modifier = Modifier
                .padding(top = 8.dp, start = 8.dp)  // Adjust this padding as needed
                .align(Alignment.TopStart)  // This will position the button in the top-left corner
        ) {
            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Navigation", Modifier.size(36.dp))
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    icons.forEachIndexed { index, icon ->
                        FilledIconButton(
                            onClick = {
                                val updatedMarker = selectedEnhancedMarker?.copy(
                                    properties = mutableStateOf(
                                        selectedEnhancedMarker.properties.value.copy(icon = index)
                                    )
                                )
                                if (updatedMarker != null && selectedMarkerId.value != null) {
                                    markers.value = markers.value.toMutableMap().apply {
                                        this[selectedMarkerId.value!!] = updatedMarker
                                    }
                                }
                            },
                            enabled = (index !=  selectedEnhancedMarker.properties.value.icon),
                            colors = filledIconButtonColors(
                                containerColor = Color.LightGray,
                                disabledContainerColor = YellowPrimary
                            )
                        ) {
//                            val tint by animateColorAsState(if (iconIndex == index) Color(0xFFEC407A) else Color(0xFFB0BEC5))
                            Icon(
                                painter = icon,
                                contentDescription = "Home Marker",
                                tint = Color.Unspecified,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
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
                        modifier = Modifier
                            .padding(all = 16.dp)
                            .width(240.dp)
                    )
                    Text(
                        text = "${(selectedEnhancedMarker?.properties?.value?.radius ?: 100f).toInt()} meters",
                        modifier = Modifier
                            .padding(all = 8.dp)
                            .align(Alignment.CenterVertically)
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Button(
                        // Delete marker for this child
                        onClick = {
//                            markers.value = markers.value.remove(selectedMarkerId.value)
                            markers.value = markers.value.filter {
                                it.key != selectedMarkerId.value
                            }
                            familyId?.let { famId ->
                                username?.let { objId ->
                                    familyLocationDao.pushMarkersToChild(famId, objId, markers.value)
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
                                    familyLocationDao.pushMarkersToChild(famId, objId, markers.value)
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
}
fun generateSmallIcons(context: Context, icons: List<Int>): List<Bitmap> {
    val height = 150
    val width = 150

    return icons.map { resourceId ->
        val bitmap = BitmapFactory.decodeResource(context.resources, resourceId)
        Bitmap.createScaledBitmap(bitmap, width, height, false)
    }
}

private fun computeCameraUpdate(markers: Iterable<LatLng>): CameraUpdate? {
    Log.d("MapMarkerOverlay", "Computing updates for $markers")
    val markersList = markers.toList()
    if (markersList.isEmpty()) {
        return null
    }
    if (markersList.size == 1) {
        return CameraUpdateFactory.newCameraPosition(
            CameraPosition(markersList[0], 15f, 0f, 0f)
        )
    }
    val builder = LatLngBounds.builder()
    markersList.forEach {
        builder.include(it)
    }
    val bounds = builder.build()
    return CameraUpdateFactory.newLatLngBounds(bounds, 500, 1000, 4)
}
