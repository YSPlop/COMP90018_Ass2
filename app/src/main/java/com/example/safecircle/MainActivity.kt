package com.example.safecircle

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.safecircle.ui.theme.SafeCircleTheme
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val firebase: DatabaseReference = FirebaseDatabase.getInstance().getReference();
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