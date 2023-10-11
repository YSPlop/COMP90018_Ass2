package com.example.safecircle.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.safecircle.database.FamilyLocationDao
import com.example.safecircle.ui.screen.EnhancedMarkerState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState


class CircleViewModel(
    private val familyId: String,
    private val username: String
): ViewModel() {
    val markers = mutableStateOf(mutableMapOf<Int, EnhancedMarkerState>())
    val lastKnownMarkers = mutableStateOf(mutableMapOf<Int, EnhancedMarkerState>())
    var memberLocations by mutableStateOf(mapOf<String, LatLng>())
        private set
    private var _familyLocationDao = FamilyLocationDao.getInstance(familyId)
    val cameraState: CameraPositionState
    init {
        _familyLocationDao.listenForCurrentLocationChanges {
            Log.d("LocationShringScreenViewModel", it.toString())
            memberLocations = it

        }

        val uniMelbCoord = LatLng(-37.798919,144.964232)
        val cameraPosition = CameraPosition.fromLatLngZoom(uniMelbCoord, 15f)
        cameraState = CameraPositionState(cameraPosition)
    }
    fun fetchMarkers(){
        _familyLocationDao.getMarkersFromChild(familyId, username){retrievedMarkers ->
            if(retrievedMarkers != null){
                markers.value = retrievedMarkers
                // Update the last marker state to current
                lastKnownMarkers.value = markers.value.toMutableMap()
            }
        }
    }
}
