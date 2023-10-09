package com.example.safecircle.database

import android.icu.text.Transliterator
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.safecircle.sensors.ForegroundSensorService
import com.example.safecircle.ui.screen.EnhancedMarkerState
import com.example.safecircle.ui.screen.MarkerProperties
import com.example.safecircle.ui.screen.PersonInfo
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.snapshots
import com.google.maps.android.compose.MarkerState
import java.util.UUID

class FamilyDatabase {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val familiesReference: DatabaseReference = database.getReference("families")

    companion object {
        @Volatile
        private var instance: FamilyDatabase? = null
        fun getInstance() = instance ?: synchronized(this) {
            instance ?: FamilyDatabase().also { instance = it }
        }
    }

    fun familyExists(familyId: String, onComplete: (Boolean) -> Unit) {

        familiesReference.child(familyId).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                onComplete(dataSnapshot.exists())
            }

            override fun onCancelled(databaseError: DatabaseError) {
                onComplete(false)
            }
        })
    }

    fun usernamePasswordMatch(
        username: String,
        password: String,
        family: String,
        onComplete: (Boolean, String?) -> Unit
    ) {
        val parentsRef = familiesReference.child(family).child("parents")

        parentsRef.orderByChild("username").equalTo(username)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (parentSnapshot in dataSnapshot.children) {
                            val dbUsername =
                                parentSnapshot.child("username").getValue(String::class.java)
                            val dbPassword =
                                parentSnapshot.child("password").getValue(String::class.java)

                            if (username == dbUsername && password == dbPassword) {
                                onComplete(true, parentSnapshot.key)  // Return the key (objectID)
                                return
                            }
                        }
                        onComplete(false, null)  // No match found after iterating.
                    } else {
                        onComplete(false, null)  // No user found with the given username.
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    onComplete(false, null)  // Some error occurred.
                }
            })
    }

    fun codeMatch(code: String, family: String, onComplete: (Boolean, String?, String?) -> Unit) {
        val childRef = familiesReference.child(family).child("child")

        childRef.orderByChild("code").equalTo(code)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (childSnapshot in dataSnapshot.children) {
                            val dbCode = childSnapshot.child("code").getValue(String::class.java)

                            if (code == dbCode) {
                                val username = childSnapshot.child("username")
                                    .getValue(String::class.java)  // Fetch the username
                                onComplete(
                                    true,
                                    childSnapshot.key,
                                    username
                                )  // Return the key (objectID) and username
                                return
                            }
                        }
                        onComplete(false, null, null)  // No match found after iterating.
                    } else {
                        onComplete(false, null, null)  // No entry found with the given code.
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    onComplete(false, null, null)  // Some error occurred.
                }
            })
    }

    fun addParentToFamily(familyId: String, parent: Parent, onSuccess: (String) -> Unit) {
        val parentId = familiesReference.child(familyId).child("parents").push().key ?: return

        familiesReference.child(familyId).child("parents").child(parentId).setValue(parent)
            .addOnSuccessListener {
                Log.i("test", "Parent added to Firebase Database under 'parents' key")
                onSuccess(parentId)
            }
            .addOnFailureListener { e ->
                Log.e("test", "Fail to add parent", e)
            }
    }

    fun addNewParentToFamily(
        familyId: String,
        parent: Parent,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        // First, check if there is a parent with the same username under the family
        familiesReference.child(familyId).child("parents").orderByChild("username")
            .equalTo(parent.username)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // If a parent with the same username exists, then notify the caller
                    if (dataSnapshot.exists()) {
                        onFailure("Parent with the same username already exists.")
                        return
                    }

                    // Generate a new parent ID for the new entry
                    val parentId = familiesReference.child(familyId).child("parents").push().key
                    if (parentId == null) {
                        onFailure("Failed to generate a unique ID for the parent.")
                        return
                    }

                    // Otherwise, add the parent to the database using the generated parent ID
                    familiesReference.child(familyId).child("parents").child(parentId)
                        .setValue(parent)
                        .addOnSuccessListener {
                            Log.i(
                                "test",
                                "New parent added to Firebase Database under 'parents' key"
                            )
                            onSuccess()
                        }
                        .addOnFailureListener { e ->
                            Log.e("test", "Fail to add parent", e)
                            onFailure("Failed to add parent due to database error.")
                        }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e(
                        "test",
                        "Database error when checking for parent username",
                        databaseError.toException()
                    )
                    onFailure("Failed due to database error.")
                }
            })
    }

    // Function to add/update personal details to Firebase
    fun savePersonalDetails(
        familyId: String,
        objectId: String,
        details: PersonalDetails,
        onComplete: (Boolean, String?) -> Unit
    ) {
        familiesReference.child(familyId).child("parents").child(objectId).child("personalDetails")
            .setValue(details)
            .addOnSuccessListener {
                onComplete(true, "Details saved successfully!")
            }
            .addOnFailureListener { e ->
                onComplete(false, e.localizedMessage)
            }
    }

    // Function to retrieve personal details from Firebase
    fun getPersonalDetails(
        familyId: String,
        objectId: String,
        onComplete: (PersonalDetails?) -> Unit
    ) {
        familiesReference.child(familyId).child("parents").child(objectId).child("personalDetails")
            .addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val details = dataSnapshot.getValue(PersonalDetails::class.java)
                    onComplete(details)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    onComplete(null)
                }
            })
    }

    fun getAllParentsDetails(familyId: String, onComplete: (List<PersonalDetails>) -> Unit) {
        val parentsRef = familiesReference.child(familyId).child("parents")

        parentsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val parentsList = mutableListOf<PersonalDetails>()

                dataSnapshot.children.forEach { parentSnapshot ->
                    val parent = parentSnapshot.getValue(Parent::class.java)
                    parent?.personalDetails?.let { parentsList.add(it) }
                }

                onComplete(parentsList)
            }


            override fun onCancelled(databaseError: DatabaseError) {
                // Handle the error accordingly, possibly calling onComplete with an empty list.
                onComplete(listOf())
            }
        })
    }

    fun getAllChildrenInfo(familyId: String, onComplete: (List<PersonInfo>) -> Unit) {
        val childrenRef = familiesReference.child(familyId).child("child")
        childrenRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val childrenList = mutableListOf<PersonInfo>()

                for (childSnapshot in snapshot.children) {
                    val child = childSnapshot.getValue(Child::class.java)
                    if (child != null) {
                        val name: String =
                            if (child.username != null) child.username!! else "Unknown"
                        childrenList.add(
                            PersonInfo(
                                name,
                                "placeholder location",
                                child.temperature.toString(),
                                child.battery.toString()
                            )
                        )
                    }
                }

                onComplete(childrenList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("test", "Error fetching parent", databaseError.toException())
                onComplete(listOf())
            }
        })
    }


    fun addChildToFamily(familyId: String, child: Child) {
        val childId = familiesReference.child(familyId).child("children").push().key ?: return
        Log.i("test", "Child add function called: famID: $familyId, child: ${child.code}")
        familiesReference.child(familyId).child("child").child(childId).setValue(child)
            .addOnSuccessListener {
                Log.i("test", "Child added to Firebase Database under 'child' key")
            }
            .addOnFailureListener { e ->
                Log.e("test", "Fail to add child", e)
            }
    }

    fun updateParentTemperature(familyId: String, username: String, temperature: Float) {
        val parentRef = familiesReference.child(familyId).child("parents").orderByChild("username")
            .equalTo(username)

        parentRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (ds in dataSnapshot.children) {
                        ds.ref.child("temperature").setValue(temperature)
                            .addOnSuccessListener {
                                Log.i("test", "Temperature updated in Firebase Database for parent")
                            }
                            .addOnFailureListener { e ->
                                Log.e("test", "Fail to update temperature", e)
                            }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("test", "Error fetching parent", databaseError.toException())
            }
        })
    }

    /**
     * Sets the value of the temperature field for a child in a family.
     */
    fun setChildTemperature(familyId: String?, username: String?, temperature: Float) {
        if (familyId == null || username == null) return

        val childrenRef = familiesReference.child(familyId).child("child")
        childrenRef.orderByChild("username").equalTo(username)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (s in snapshot.children) {
                            s.ref.child("temperature").setValue(temperature)
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("FamilyDatabase", "Error: $databaseError")
                }
            })

        Log.i("FamilyDatabase", "Set child temperature: " + temperature)
    }

    /**
     * Sets the value of the battery field for a child in a family.
     */
    fun setChildBattery(familyId: String?, username: String?, battery: Float) {
        if (familyId == null || username == null) return

        val childrenRef = familiesReference.child(familyId).child("child")
        childrenRef.orderByChild("username").equalTo(username)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (s in snapshot.children) {
                            s.ref.child("battery").setValue(battery)
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("FamilyDatabase", "Error: $databaseError")
                }
            })

        Log.i("FamilyDatabase", "Set child battery: " + battery)
    }

    fun pushMarkersToChild(familyId: String?, objectId: String?, markersMap: Map<Int, EnhancedMarkerState>) {
        if (familyId == null || objectId == null) return
        val childRef = familiesReference.child(familyId).child("child").child(objectId)
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
                Log.i("FamilyDatabase", "Successfully set markers group for family ID: $familyId")
            }
            .addOnFailureListener { exception ->
                Log.e("FamilyDatabase", "Error: $exception")
            }
    }

    fun getMarkersFromChild(familyId: String?, objectId: String?, markers: MutableState<MutableMap<Int, EnhancedMarkerState>>) {
        if (familyId == null || objectId == null) return

        val childRef = familiesReference.child(familyId).child("child").child(objectId)

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
            markers.value = markersMap
        }
            .addOnFailureListener { exception ->
                Log.e("FamilyDatabase", "Error: $exception")
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
//    data class FirebaseMarker(
//        val id: UUID = UUID.randomUUID(),
//        val lat: Double = 0.0,
//        val lng: Double = 0.0,
//        val radius: Float = 0f,
//        val name: String = ""
//    )
}
