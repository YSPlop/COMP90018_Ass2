package com.example.safecircle.ui.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.safecircle.ui.components.map.MapMarkerOverlay
import com.example.safecircle.utils.ErrorDialog
import com.example.safecircle.viewmodel.MapViewModel
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState


@Composable
fun MapsScreen(navController: NavController) {

    val context = LocalContext.current;
    //    val familyId = PreferenceHelper(context).getFamilyID();
    val familyId = "testFamily"
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


    GoogleMap(
        modifier = Modifier
            .safeContentPadding()
            .fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        MapMarkerOverlay(viewModel = viewModel)
    }
}


