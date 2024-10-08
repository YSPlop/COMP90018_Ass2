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
fun MapMarkerOverlay(viewModel: MapViewModel, username: String?=null) {
    Log.d("MapMarkerTest","username is: $username");
    val cameraPositionState = viewModel.cameraState
    var dataLoaded by remember {
        mutableStateOf(false)
    }
    val memberLocations = viewModel.memberLocations.filter {
        if (username.isNullOrEmpty()) {
            true
        } else {
            it.key == username
        }
    }

    Log.d("MapMarkerOverlay", "memberLocations = $memberLocations")
    memberLocations.entries
        .map {
        val name = shortenUsername(it.key)
        MarkerComposable(
            state = MarkerState(it.value),
            title = it.key,
            ) {
            TextIcon(text = name)
        }
    }
}

private fun shortenUsername(username: String): String {
    if (username.isEmpty() && username.isBlank()) {
        return "U"
    }
    val parts = username.split(" ");
    if (parts.size > 1) {
        return parts.joinToString(separator = "") { it[0].toString() }
    }
    if (parts[0].length > 1) {
        return parts[0].substring(0, 2).uppercase()
    }
    return parts[0].first().uppercase()
}

