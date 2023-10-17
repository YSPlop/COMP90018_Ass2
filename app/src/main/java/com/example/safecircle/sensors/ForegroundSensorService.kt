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
import com.example.safecircle.database.FamilyLocationDao
import com.example.safecircle.database.Role
import com.example.safecircle.interfaces.LocationClient
import com.example.safecircle.utils.DefaultLocationClient
import com.example.safecircle.utils.PreferenceHelper
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class ForegroundSensorService: Service()  {

    // Detects temperature.
    private lateinit var temperatureSensorManager: TemperatureSensorManager
    var isTemperatureSensorAvailable: Boolean by mutableStateOf(false)
        private set
    var temperatureValue: Float by mutableFloatStateOf(0.0f)
        private set
//
//    // Detects ambient noise.
//    lateinit var noiseSensorManager: NoiseSensorManager private set
//    var isNoiseSensorAvailable: Boolean by mutableStateOf(false)
//        private set
//    var noiseValue: Double by mutableDoubleStateOf(0.0)
//        private set
//
//
    // Detects battery percentage.
    private lateinit var batterySensorManager: BatterySensorManager
    var isBatterySensorAvailable: Boolean by mutableStateOf(false)
        private set
    var batteryValue: Float by mutableFloatStateOf(100.0f)
        private set

    // Updates realtime sensor data to database at a set interval.
    var sensorDataPushManager: SensorDataPushManager? = null

    // Properties for location updates
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var familyId: String
    private lateinit var memberId: String
    private lateinit var locationDao: FamilyLocationDao
    private lateinit var locationClient: LocationClient


    companion object{
        @Volatile
        private var instance: ForegroundSensorService? = null
        fun getInstance() = instance
    }

    override fun onCreate() {
        super.onCreate()
        // Initialize location client here, moved from LocationPushService
        locationClient = DefaultLocationClient(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext)
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = NotificationCompat.Builder(this, "SensorChannel")
            .setContentTitle("Monitoring: Location & Battery & Temperature")
            .setSmallIcon(R.drawable.family) // replace with your icon
            .build()

        startForeground(1, notification)

        Log.i("test", "Foreground service Started")

        temperatureSensorManager = TemperatureSensorManager(this) { available, value ->
            isTemperatureSensorAvailable = available
            temperatureValue = value
        }

        batterySensorManager = BatterySensorManager(this) { batteryPercentage ->
            batteryValue = batteryPercentage
        }
        batterySensorManager.registerListener()
        isBatterySensorAvailable = true

        // Create a new instance
        sensorDataPushManager = SensorDataPushManager(this, this)
        //initialize the sensorDataPushManager
        sensorDataPushManager!!.start()

        instance = this

        // Start getting location updates
        val preferenceHelper = PreferenceHelper(this)
        familyId = preferenceHelper.getFamilyID().toString()
        memberId = preferenceHelper.getUsername().toString()
        locationDao = FamilyLocationDao.getInstance(familyId)

        locationClient.getLocationUpdates(10.toDuration(DurationUnit.SECONDS))
            .catch { e ->
                Log.e("Location Service", e.toString())
            }
            .onEach {
                Log.d("Location Service", "lat = ${it.latitude}, lng = ${it.longitude}")
                locationDao.updateCurrentMemberLocation(memberId, it.latitude, it.longitude)
            }
            .launchIn(serviceScope)

        return START_NOT_STICKY
    }

//    fun startNoiseSensor() {
//        noiseSensorManager = NoiseSensorManager(this){value ->
//            noiseValue = value
//            //Log.i("test", "Noise sensor value: " + value)
//            if(noiseValue > 600) sendLoudNoiseNotification();
//        }
//        noiseSensorManager.init()
//        isNoiseSensorAvailable = true
//    }
//
//    fun stopNoiseSensor() {
//        isNoiseSensorAvailable = false
//        noiseSensorManager?.end()
//    }
//

    private fun sendLoudNoiseNotification() {
        val notification = NotificationCompat.Builder(this, "SensorChannel")
            .setContentTitle("Loud Noise Detected")
            .setContentText("")
            .setSmallIcon(R.drawable.family) // replace with your icon
            .build()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(3, notification)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        temperatureSensorManager.unregisterListener()
        batterySensorManager.unregisterListener()
        // Cleanup the old instance if it exists
        sensorDataPushManager?.cleanup()
        sensorDataPushManager = null
        Log.i("test", "Foreground service Stopped")
    }
}