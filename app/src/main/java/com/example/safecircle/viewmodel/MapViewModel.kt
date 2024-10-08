package com.example.safecircle.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.safecircle.database.FamilyLocationDao
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState


class MapViewModel(
    private val familyId: String
): ViewModel() {
    var memberLocations by mutableStateOf(mapOf<String, LatLng>())
        private set
    private var _familyLocationDao = FamilyLocationDao(familyId)
    val cameraState: CameraPositionState
    init {
        _familyLocationDao.listenForCurrentLocationChanges {
            Log.d("LocationShringScreenViewModel", it.toString())
            memberLocations = it
        }
        Log.d("LocationDatabase", "Location Database Contructed FamID=$familyId")
        val uniMelbCoord = LatLng(-37.798919,144.964232)
        val cameraPosition = CameraPosition.fromLatLngZoom(uniMelbCoord, 15f)
        cameraState = CameraPositionState(cameraPosition)
    }
     fun fetchMemberLocationsAsync() {
         _familyLocationDao.getMembersLocationsAsync { memberLocations = it }
     }

    fun fetchMemberLocationsAsync(callback: (Map<String, LatLng>) -> Unit) {
        _familyLocationDao.getMembersLocationsAsync {
            Log.d("LocationDatabase", "membersLocations: $it")
            memberLocations = it
            callback(it);
        }
    }


}
