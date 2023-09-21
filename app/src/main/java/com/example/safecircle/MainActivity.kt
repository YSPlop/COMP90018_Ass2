package com.example.safecircle

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.safecircle.sensors.ForegroundSensorService
import com.example.safecircle.ui.theme.SafeCircleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val serviceIntent = Intent(this, ForegroundSensorService::class.java)
        startService(serviceIntent)

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
            SafeCircleTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavigationComposable(navController = navController)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        // Define the notification channel
                        val channelId = "battery_notification_channel"
                        val name = "Battery Notification"
                        val importance = NotificationManager.IMPORTANCE_DEFAULT
                        val mChannel = NotificationChannel(channelId, name, importance)

                        // Register the channel with the system
                        val notificationManager = getSystemService(NotificationManager::class.java)
                        notificationManager.createNotificationChannel(mChannel)
                    }
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
