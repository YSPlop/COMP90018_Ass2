package com.example.safecircle.ui.components.map


import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.safecircle.ui.components.TextIcon
import com.example.safecircle.viewmodel.MapViewModel
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.MarkerState

@Composable
fun MapMarkerOverlay(viewModel: MapViewModel) {
    val cameraPositionState = viewModel.cameraState
    var dataLoaded by remember {
        mutableStateOf(true)
    }
    LaunchedEffect(viewModel.memberLocations) {
        if (!dataLoaded) {
            viewModel.fetchMemberLocationsAsync()
            dataLoaded = true
        }
        Log.d("MapMarkerOverlay", "LaunchedEffect triggered");
        val cameraUpdate = computeCameraUpdate(viewModel.memberLocations.values)
        if (cameraUpdate != null) {
            cameraPositionState.animate(cameraUpdate, 500)
        }
    }
    viewModel.memberLocations.entries.map {
        val name = it.key
            .split(" ")
            .joinToString(separator = "") { p -> p[0].uppercase() }
        MarkerComposable(
            state = MarkerState(it.value),
            title = it.key,

            ) {
            TextIcon(text = name)
        }
    }
}

private fun computeCameraUpdate(markers: Iterable<LatLng>): CameraUpdate? {
    val markersList = markers.toList()
    if (markersList.isEmpty()) {
        return null
    }
    if (markersList.size == 1) {
        return CameraUpdateFactory.newLatLngZoom(markersList[0], 10f)
    }
    val builder = LatLngBounds.builder()
    markersList.forEach {
        builder.include(it)
    }
    val bounds = builder.build()
    return CameraUpdateFactory.newLatLngBounds(bounds, 500, 1000, 4)
}