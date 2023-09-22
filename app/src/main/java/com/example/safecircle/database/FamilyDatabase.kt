package com.example.safecircle.database

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FamilyDatabase {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val familiesReference: DatabaseReference = database.getReference("families")

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
    fun usernamePasswordMatch(username: String, password: String, family: String, onComplete: (Boolean) -> Unit) {
        // Assuming familyReference points to the root node.
        val familyRef = familiesReference.child(family).child("parent")

        familyRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val dbUsername = dataSnapshot.child("username").getValue(String::class.java)
                val dbPassword = dataSnapshot.child("password").getValue(String::class.java)

                if (username == dbUsername && password == dbPassword) {
                    onComplete(true)
                } else {
                    onComplete(false)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                onComplete(false)
            }
        })
    }

    fun codeMatch(code: String, family: String, onComplete: (Boolean) -> Unit) {
        // Assuming familyReference points to the root node.
        val childRef = familiesReference.child(family).child("child")

        childRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val dbCode = dataSnapshot.child("code").getValue(String::class.java)

                if (code == dbCode) {
                    onComplete(true)
                } else {
                    onComplete(false)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                onComplete(false)
            }
        })
    }

    fun addParentToFamily(familyId: String, parent: Parent) {
        val parentId = familiesReference.child(familyId).child("parents").push().key ?: return

        familiesReference.child(familyId).child("parents").child(parentId).setValue(parent)
            .addOnSuccessListener {
                Log.i("test", "Parent added to Firebase Database under 'parents' key")
            }
            .addOnFailureListener { e ->
                Log.e("test", "Fail to add parent", e)
            }
    }

    fun addChildToFamily(familyId: String, child: Child) {
        val childId = familiesReference.child(familyId).child("children").push().key ?: return

        familiesReference.child(familyId).child("child").child(childId).setValue(child)
            .addOnSuccessListener {
                Log.i("test", "Child added to Firebase Database under 'child' key")
            }
            .addOnFailureListener { e ->
                Log.e("test", "Fail to add child", e)
            }
    }

    fun updateParentTemperature(familyId: String, username: String, temperature: Float) {
        val parentRef = familiesReference.child(familyId).child("parents").orderByChild("username").equalTo(username)

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


}
