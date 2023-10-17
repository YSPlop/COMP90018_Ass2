package com.example.safecircle.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.safecircle.database.Role

class PreferenceHelper(context: Context) {

    private val PREFS_NAME = "com.example.safecircle.preferences"
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        const val FAMILY_ID = "FAMILY_ID"
        const val USERNAME = "USERNAME"
        const val OBJECT_ID = "OBJECT_ID"
        const val ROLE = "ROLE"
        const val EMERGENCY_CONTACT = "EMERGENCY_CONTACT"
        const val LAST_OBJECT_ID = "LAST_OBJECT_ID"
    }

    fun setFamilyID(familyID: String) {
        val editor = prefs.edit()
        editor.putString(FAMILY_ID, familyID)
        editor.apply()
    }

    fun getFamilyID(): String? {
        return prefs.getString(FAMILY_ID, null)
    }

    fun setUsername(username: String) {
        val editor = prefs.edit()
        editor.putString(USERNAME, username)
        editor.apply()
    }

    fun getUsername(): String? {
        return prefs.getString(USERNAME, null)
    }

    // For OBJECT_ID
    fun setObjectId(objectId: String) {
        val editor = prefs.edit()
        editor.putString(OBJECT_ID, objectId)
        editor.apply()
    }

    fun getObjectId(): String? {
        return prefs.getString(OBJECT_ID, null)
    }

    // For ROLE
    fun setRole(role: Role) {
        val editor = prefs.edit()
        editor.putString(ROLE, role.name)
        editor.apply()
    }

    fun getRole(): Role? {
        val roleStr = prefs.getString(ROLE, null)
        return if (roleStr != null) Role.valueOf(roleStr) else null
    }

    fun setEmergencyContact(number: String) {
        val editor = prefs.edit()
        editor.putString(EMERGENCY_CONTACT, number)
        editor.apply()
    }

    fun getEmergencyContact(): String? {
        return prefs.getString(EMERGENCY_CONTACT, null)
    }

    fun clearPreferences() {
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
    }

    // For LAST_OBJECT_ID
    fun setLastObjectID(objectID: String) {
        val editor = prefs.edit()
        editor.putString(LAST_OBJECT_ID, objectID)
        editor.apply()
    }

    fun getLastObjectID(): String? {
        return prefs.getString(LAST_OBJECT_ID, null)
    }

}
