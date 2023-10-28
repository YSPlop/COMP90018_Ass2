package com.example.safecircle

import android.Manifest
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.compose.rememberNavController
import com.example.safecircle.ui.theme.SafeCircleTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState


class MainActivity : ComponentActivity() {
//    private val requestRecordAudioPermissionLauncher =
//        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
//            ForegroundSensorService.getInstance()?.startNoiseSensor()
//        }

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Notification Channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "SensorChannel",
                "Sensor Monitoring",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        setContent {
            val permissions = rememberMultiplePermissionsState(permissions = listOf(
                Manifest.permission.SEND_SMS,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.POST_NOTIFICATIONS,
                Manifest.permission.RECORD_AUDIO,
            ))
            val backgroundLocationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
            var askedPermission by remember {
                mutableStateOf(false)
            }
            val context = this;
            val lifecycleOwner = LocalLifecycleOwner.current
            DisposableEffect(lifecycleOwner) {
                val observer = LifecycleEventObserver { source, event ->
                    when(event) {
                        Lifecycle.Event.ON_START -> {
                            if (!permissions.allPermissionsGranted and !askedPermission) {
                                Log.d("PermissionRequest", "Asking for permissions")
                                permissions.launchMultiplePermissionRequest()
                                askedPermission = true;
                            }

                            if (backgroundLocationPermissionState.status != PermissionStatus.Granted && !askedPermission) {
                                AlertDialog.Builder(context)
                                    .setMessage(
                                        "We need background location permission to share your " +
                                                "realtime location with other family members. Click" +
                                                "\"OK\" to enable it in System Settings"
                                    ).setPositiveButton("OK") { dialog, _ ->
                                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                        intent.data =
                                            Uri.fromParts("package", packageName, null)
                                        startActivity(intent)
                                        dialog.dismiss()
                                    }.setNegativeButton("Cancel") {dialog, _ ->
                                        dialog.dismiss()
                                    }
                                        .create().show()
                            }

                        }
                        else -> {}
                    }
                }
                lifecycleOwner.lifecycle.addObserver(observer)
                onDispose {
                    lifecycleOwner.lifecycle.removeObserver(observer)
                }
            }
            if (!permissions.allPermissionsGranted && askedPermission) {
                Toast.makeText(
                    context,
                    "We don't have all permissions, please approve them in settings",
                    Toast.LENGTH_SHORT
                )
                    .show()
                Log.d("PermissionsRequest", "revoked Permissions: ${permissions.revokedPermissions}")
            }
            if (backgroundLocationPermissionState.status == PermissionStatus.Granted && askedPermission) {
                Toast.makeText(
                    context,
                    "We need background location permission to show your kids' location",
                    Toast.LENGTH_SHORT)
                    .show()
            }
            SafeCircleTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavigationComposable(navController = navController)
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        //stopService(Intent(this, ForegroundSensorService::class.java))
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SafeCircleTheme {
        Greeting("Android")
    }
}

@Composable
fun SensorScreen(
    isTemperatureSensorAvailable: Boolean,
    temperatureValue: Float,
) {
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isTemperatureSensorAvailable) {
                Text(text = "Temperature: $temperatureValue")
            } else {
                Text(text = "Temperature Sensor is not available")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SensorScreenPreview() {
    SafeCircleTheme {
        SensorScreen(
            isTemperatureSensorAvailable = true,
            temperatureValue = 25.0f
        )
    }
}
