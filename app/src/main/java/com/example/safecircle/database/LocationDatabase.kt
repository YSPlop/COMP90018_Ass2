package com.example.safecircle.database

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import com.example.safecircle.database.dtos.LocationDto
import com.example.safecircle.ui.screen.EnhancedMarkerState
import com.example.safecircle.ui.screen.MarkerProperties
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.maps.android.compose.MarkerState

class FamilyLocationDao private constructor (
    private val familyId: String
){
    companion object{
        private const val COLLECTION_NAME = "test_locations"
        private const val LOG_TAG = "FamilyLocationDao"
        private var instance: FamilyLocationDao? = null
        fun getInstance(familyId: String): FamilyLocationDao {
            if (instance == null) {
                instance = FamilyLocationDao(familyId)
            }
            return instance!! // Trust me, it's not null
        }
    }


    private val locationCollection: DatabaseReference = FirebaseDatabase
        .getInstance()
        .getReference(COLLECTION_NAME)

    fun updateCurrentMemberLocation(memberId: String, lat: Double, lng: Double) {
        locationCollection.child(familyId).child(memberId).updateChildren(mapOf(
            "currentLocation" to mapOf(
                "lat" to lat,
                "lng" to lng
            )
        )).addOnCompleteListener{
            Log.d(LOG_TAG,"update completed")
        }
    }

//    fun updateCurrentMemberLocation(memberId: String, location: LatLng) {
//        updateCurrentMemberLocation(memberId, location.latitude, location.longitude)
//    }

    fun listenForCurrentLocationChanges(listener: (Map<String, LatLng>) -> Unit) {
        val eventListener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val locations = memberSnapshot2HashMap(snapshot)
                listener(locations)
            }


            override fun onCancelled(error: DatabaseError) {
                Log.e("LocationDatabase", error.toString())
            }

        }
        locationCollection.child(familyId).addValueEventListener(
            eventListener
        )
    }
    fun getMembersLocationsAsync(onComplete: (Map<String, LatLng>) -> Unit){
        var task = locationCollection.child(familyId).get()
        task.addOnSuccessListener {
            memberSnapshot2HashMap(it).apply(onComplete)
        }
    }



    private fun memberSnapshot2HashMap(snapshot: DataSnapshot): Map<String, LatLng> {
        val pairs: List<Pair<String, LatLng>> = snapshot.children
            .filter { it.key != null && it.value != null }
            .map { Log.d("FamilyLocationDao", "it = ${it.key}"); it }
            .map {
                val key = it.key!!
                val location = it.child("currentLocation").getValue(LocationDto::class.java)!!
                val latlng = LatLng(location.lat!!, location.lng!!)
                key to latlng
            }
        return pairs.associate { it }
    }

    fun pushMarkersToChild(familyId: String?, username: String?, markersMap: Map<Int, EnhancedMarkerState>) {
        if (familyId == null || username == null) return
        val childRef = locationCollection.child(familyId).child(username)
        // Convert your markers map to a list suitable for firebase
        val markersList = markersMap.map {
            FirebaseMarker(
                id = it.key,
                lat = it.value.markerState.position.latitude,
                lng = it.value.markerState.position.longitude,
                radius = it.value.properties.value.radius,
                name = it.value.properties.value.name
            )
        }

        childRef.child("markers").setValue(markersList)
            .addOnSuccessListener {
                Log.i("LocationDatabase",
                    "Successfully set markers group for family ID: $familyId child: $username"
                )
            }
            .addOnFailureListener { exception ->
                Log.e("LocationDatabase", "Error: $exception")
            }
    }

    fun getMarkersFromChild(
        familyId: String?,
        username: String?,
        callback: (MutableMap<Int, EnhancedMarkerState>?) -> Unit
    ){
        if (familyId == null || username == null) {
            callback(null)
            return
        }

        val childRef = locationCollection.child(familyId).child(username)

        childRef.child("markers").get().addOnSuccessListener { dataSnapshot ->
            val markersMap = mutableMapOf<Int, EnhancedMarkerState>()
            dataSnapshot.children.forEach { childSnapshot ->
                val firebaseMarker = childSnapshot.getValue(FirebaseMarker::class.java)
                if (firebaseMarker != null) {
                    val markerState = MarkerState(position= LatLng(firebaseMarker.lat, firebaseMarker.lng))
                    val properties = MarkerProperties(firebaseMarker.radius, firebaseMarker.name)
                    markersMap[firebaseMarker.id] = EnhancedMarkerState(markerState, mutableStateOf(properties))
                }
            }
            callback(markersMap)
        }
            .addOnFailureListener { exception ->
                Log.e("FamilyDatabase", "Error: $exception")
                callback(null)
            }
    }

    data class FirebaseMarker(
        val id: Int,
        val lat: Double,
        val lng: Double,
        val radius: Float,
        val name: String
    ){
        // This constructor is for Firebase
        constructor() : this(
            id = 0,
            lat = 0.0,
            lng = 0.0,
            radius = 0f,
            name = ""
        )
    }

}