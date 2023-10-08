package com.example.safecircle.ui.screen

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.safecircle.ui.components.map.MapMarkerOverlay
import com.example.safecircle.utils.ErrorDialog
import com.example.safecircle.utils.PreferenceHelper
import com.example.safecircle.viewmodel.MapViewModel
import com.google.maps.android.compose.GoogleMap


@Composable
fun MapsScreen(navController: NavController, username: String? = null) {
    Log.d("MapScreen", "Received username = $username")
    val context = LocalContext.current;
    val familyId = PreferenceHelper(context).getFamilyID();
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


    GoogleMap(
        modifier = Modifier
            .safeContentPadding()
            .fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        MapMarkerOverlay(viewModel = viewModel, username = username)
    }
}


