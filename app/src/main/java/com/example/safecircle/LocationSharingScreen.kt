package com.example.safecircle

import android.Manifest
import android.content.Intent
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.safecircle.db.FamilyLocationDao
import com.example.safecircle.services.LocationPushService
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.maps.model.LatLng

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LocationSharingScreen(navController: NavController) {
    val locationPermissionState = rememberMultiplePermissionsState(listOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    ))
    val context = LocalContext.current
    var memberId by remember {
        mutableStateOf("")
    }
    val viewModel = viewModel<LocationSharingScreenViewModel>(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T: ViewModel> create(modelClass: Class<T>): T {
                return LocationSharingScreenViewModel("testFamily") as T
            }
        }
    )
    Surface(
        Modifier
            .safeContentPadding()
            .fillMaxSize()
    ) {
        if (!locationPermissionState.allPermissionsGranted) {
            Button(
                onClick = { locationPermissionState.launchMultiplePermissionRequest() },
            ) {
                Text(text = "Get Permissions")
            }
        } else {
            Column {
                TextField(value = memberId, onValueChange = { memberId = it })
                viewModel.memberLocations.map {
                    Text(text = "${it.key}: lat: ${it.value.latitude}, lng: ${it.value.longitude}")
                }
                Button(onClick = {
                    Intent(context, LocationPushService::class.java).apply {
                        action = LocationPushService.ACTION_START
                        putExtra("familyId", "testFamily")
                        putExtra("memberId", memberId)
                        context.startForegroundService(this)
                    }
                }) {
                    Text("Launch Service")
                }
            }
        }
    }

}


class LocationSharingScreenViewModel(
   familyId: String,
): ViewModel()  {
    var memberLocations by mutableStateOf(mapOf<String, LatLng>())
        private set
    private var _familyLocationDao = FamilyLocationDao.getInstance(familyId)
    init {
        _familyLocationDao.listenForCurrentLocationChanges {
            Log.d("LocationShringScreenViewModel", it.toString())
            memberLocations = it
        }
        _familyLocationDao.getMembersLocationsAsync { memberLocations = it }

    }
}