package com.example.safecircle.sensors
import android.app.NotificationManager
import android.app.Service;
import android.content.Context
import android.content.Intent;
import android.os.IBinder;
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.NotificationCompat
import com.example.safecircle.R
import javax.inject.Singleton

class ForegroundSensorService: Service()  {
    private var temperatureValue: Float by mutableFloatStateOf(0.0f)
    private var isTemperatureSensorAvailable: Boolean by mutableStateOf(false)
    private lateinit var temperatureSensorManager: TemperatureSensorManager
    private var noiseValue: Double by mutableDoubleStateOf(0.0)
    private var isNoiseSensorAvailable: Boolean by mutableStateOf(false)
    lateinit var noiseSensorManager: NoiseSensorManager private set
    private lateinit var batterySensorManager: BatterySensorManager

    companion object{
        @Volatile
        private var instance: ForegroundSensorService? = null
        fun getInstance() = instance ?: synchronized(this){
            instance ?: ForegroundSensorService().also { instance = it }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = NotificationCompat.Builder(this, "SensorChannel")
            .setContentTitle("Monitoring Battery & Temperature")
            .setSmallIcon(R.drawable.family) // replace with your icon
            .build()

        startForeground(1, notification)

        Log.i("test", "Foreground service Started")

        temperatureSensorManager = TemperatureSensorManager(this) { available, value ->
            isTemperatureSensorAvailable = available
            temperatureValue = value
        }

        batterySensorManager = BatterySensorManager(this) { batteryPercentage ->
            if (batteryPercentage <= 30) {
                sendBatteryLowNotification()
            }
        }
        batterySensorManager.registerListener()

        // TODO: add more sensor managers
        // val anotherSensorManager = AnotherSensorManager(this) { ... }

        return START_NOT_STICKY
    }

    fun startNoiseSensor() {
        noiseSensorManager = NoiseSensorManager(this){ available, value ->
            isNoiseSensorAvailable = available
            noiseValue = value
            Log.i("test", "Noise sensor value: " + value)
        }
        noiseSensorManager.init()
    }

    fun stopNoiseSensor() {
        noiseSensorManager?.end()
    }

    private fun sendBatteryLowNotification() {
        val notification = NotificationCompat.Builder(this, "SensorChannel")
            .setContentTitle("Battery Low")
            .setContentText("Battery level is below 30%")
            .setSmallIcon(R.drawable.family) // replace with your icon
            .build()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(2, notification)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        temperatureSensorManager.unregisterListener()
        batterySensorManager.unregisterListener()
        Log.i("test", "Foreground service Stopped")
    }
}