package com.example.safecircle

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.safecircle.ui.theme.SafeCircleTheme
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MainActivity : ComponentActivity() {
    private var temperatureValue: Float by mutableFloatStateOf(0.0f)
    private var isTemperatureSensorAvailable: Boolean by mutableStateOf(false)
    private lateinit var temperatureSensorManager: TemperatureSensorManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val firebase: DatabaseReference = FirebaseDatabase.getInstance().getReference();

        temperatureSensorManager = TemperatureSensorManager(this) { available, value ->
            isTemperatureSensorAvailable = available
            temperatureValue = value
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
                }
                SensorScreen(
                    isTemperatureSensorAvailable = isTemperatureSensorAvailable,
                    temperatureValue = temperatureValue,
                )
            }
        }
    }
    override fun onPause() {
        super.onPause()
        temperatureSensorManager.unregisterListener()
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
