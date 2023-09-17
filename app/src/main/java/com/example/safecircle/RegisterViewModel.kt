package com.example.safecircle

import PreferenceHelper
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RegisterViewModel(private val context: Context) : ViewModel() {

    fun register(familyID: String, username: String, password: String) {
        val familyDatabase = FamilyDatabase()

        val parent = Parent(username = username, password = password)
        familyDatabase.addParentToFamily(familyID, parent)

        // Save to SharedPreferences
        val preferenceHelper = PreferenceHelper(context)
        preferenceHelper.setFamilyID(familyID)
        preferenceHelper.setUsername(username)

    }
}
