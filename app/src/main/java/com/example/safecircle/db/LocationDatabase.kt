package com.example.safecircle.db

import android.util.Log
import com.example.safecircle.db.dtos.LocationDto
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

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

}