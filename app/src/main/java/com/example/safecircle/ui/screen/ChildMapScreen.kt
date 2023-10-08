package com.example.safecircle.ui.screen

import android.Manifest
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.example.safecircle.ChildSettings
import com.example.safecircle.database.FamilyDatabase
import com.example.safecircle.sensors.ForegroundSensorService
import com.example.safecircle.utils.PreferenceHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChildMapScreen(navController: NavHostController) {
    val context = LocalContext.current
    val preferenceHelper = PreferenceHelper(context)
    val familyID = preferenceHelper.getFamilyID()
    val username = preferenceHelper.getUsername()
    val objectID = preferenceHelper.getObjectId()
    val role = preferenceHelper.getRole()
    val emergencyContactNumber = preferenceHelper.getEmergencyContact()
    val (childrenList, setChildrenList) = remember { mutableStateOf(listOf<PersonInfo>()) }
    val familyDatabase: FamilyDatabase = FamilyDatabase()

    fun isServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    LaunchedEffect(Unit) {
        // Start ForegroundSensorService
        if (!isServiceRunning(ForegroundSensorService::class.java)) {
            val serviceIntent = Intent(context, ForegroundSensorService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent)
            } else {
                context.startService(serviceIntent)
            }
        }
    }

    // Check permissions and start service here.
    val requestRecordAudioPermissionLauncher = rememberUpdatedState(
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {  // Permission granted
                ForegroundSensorService.getInstance()?.startNoiseSensor()
            }
        }
    ).value

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestRecordAudioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        } else {
            ForegroundSensorService.getInstance()?.startNoiseSensor()
        }
    }

    // Fetch children details on screen load
    LaunchedEffect(Unit) {
        familyDatabase.getAllChildrenInfo(familyID!!) { children ->
            Log.i("test", "$children")
            setChildrenList(children)
        }
    }

    Column(

    ) {
        Column {
            TopAppBar(
                title = { androidx.compose.material3.Text(username.toString())},
                navigationIcon = {
                    IconButton(onClick = {navController.navigate(ChildSettings.route)}) {
                        Icon(imageVector = Icons.Default.Face, contentDescription = "Child Navigation", Modifier.size(36.dp))
                    }
                }
            )
            Divider()


        }

        MapsScreen(navController = navController, username)

    }

}