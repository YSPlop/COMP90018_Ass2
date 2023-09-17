import android.content.Context
import android.content.SharedPreferences

class PreferenceHelper(context: Context) {

    private val PREFS_NAME = "com.example.safecircle.preferences"
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        const val FAMILY_ID = "FAMILY_ID"
        const val USERNAME = "USERNAME"
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
}
