package com.example.safecircle.viewmodel

import com.example.safecircle.utils.PreferenceHelper
import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.safecircle.database.Parent
import com.example.safecircle.database.FamilyDatabase

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
