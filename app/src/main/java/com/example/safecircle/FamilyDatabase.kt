package com.example.safecircle

import android.content.Context
import android.util.Log
import android.widget.Toast
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

    fun addParentToFamily(familyId: String, parent: Parent) {
        val parentId = familiesReference.child(familyId).push().key ?: return

        familiesReference.child(familyId).child(parentId).setValue(parent)
            .addOnSuccessListener {
                Log.i("test", "Parent added to Firebase Database")
            }
            .addOnFailureListener { e ->
                Log.e("test", "Fail to add parent", e)
            }
    }

    fun addChildToFamily(familyId: String, child: Child) {
        val childId = familiesReference.child(familyId).push().key ?: return

        familiesReference.child(familyId).child(childId).setValue(child)
            .addOnSuccessListener {
                Log.i("test", "Child added to Firebase Database")
            }
            .addOnFailureListener { e ->
                Log.e("test", "Fail to add child", e)
            }
    }

}
