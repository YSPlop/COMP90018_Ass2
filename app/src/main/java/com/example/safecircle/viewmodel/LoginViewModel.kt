package com.example.safecircle.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.safecircle.database.FamilyDatabase
import com.example.safecircle.database.Role
import com.example.safecircle.utils.PreferenceHelper
class LoginViewModel(private val context: Context) : ViewModel() {

    private val familyDatabase = FamilyDatabase()
    private val preferenceHelper = PreferenceHelper(context)

    fun loginAsParent(familyID: String, username: String, password: String, onComplete: (Boolean) -> Unit) {
        familyDatabase.usernamePasswordMatch(username, password, familyID) { match, objectID ->
            if (match && objectID != null) {
                preferenceHelper.apply {
                    setFamilyID(familyID)
                    setUsername(username)
                    setObjectId(objectID)
                    setRole(Role.PARENT)
                }
                onComplete(true)
            } else {
                onComplete(false)
            }
        }
    }

    fun loginAsKid(familyID: String, code: String, onComplete: (Boolean) -> Unit) {
        familyDatabase.codeMatch(code, familyID) { match, objectID, username ->
            if (match && objectID != null && username != null) {
                preferenceHelper.apply {
                    setFamilyID(familyID)
                    setUsername(username)
                    setObjectId(objectID)
                    setRole(Role.CHILD)
                }
                onComplete(true)
            } else {
                onComplete(false)
            }
        }
    }

}

