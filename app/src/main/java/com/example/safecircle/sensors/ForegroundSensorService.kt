package com.example.safecircle.sensors
import android.app.NotificationManager
import android.app.Service;
import android.content.Context
import android.content.Intent;
import android.os.IBinder;
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.NotificationCompat
import com.example.safecircle.R
import com.example.safecircle.database.FamilyDatabase
import com.example.safecircle.database.FamilyLocationDao
import com.example.safecircle.database.Role
import com.example.safecircle.interfaces.LocationClient
import com.example.safecircle.ui.screen.EnhancedMarkerState
import com.example.safecircle.utils.DefaultLocationClient
import com.example.safecircle.utils.GlobalState
import com.example.safecircle.utils.PreferenceHelper
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
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
    private lateinit var role: Role
    private lateinit var locationDao: FamilyLocationDao
    private lateinit var familyDatabase: FamilyDatabase
    private lateinit var locationClient: LocationClient
//    val markers = mutableStateOf(mapOf<Int, EnhancedMarkerState>())

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
        role = preferenceHelper.getRole()!!

        locationDao = FamilyLocationDao.getInstance(familyId)
        familyDatabase = FamilyDatabase()

        if(role == Role.CHILD) {
            locationClient.getLocationUpdates(10.toDuration(DurationUnit.SECONDS))
                .catch { e ->
                    Log.e("Location Service", e.toString())
                }
                .onEach {
                    Log.d("Location Service", "lat = ${it.latitude}, lng = ${it.longitude}")
                    locationDao.updateCurrentMemberLocation(memberId, it.latitude, it.longitude)
                    locationDao.getMarkersFromChild(familyId, memberId) { retrievedMarkers ->
                        var isInside = false
                        var markerName = ""
                        if (retrievedMarkers != null) {
                            GlobalState.markers = retrievedMarkers

                            // Check if the current location is inside any marker circle
                            for (enhancedMarker in GlobalState.markers.values) {
                                val markerLocation = enhancedMarker.markerState.position
                                val radius = enhancedMarker.properties.value.radius
                                if (isLocationInsideCircle(
                                        LatLng(it.latitude, it.longitude),
                                        markerLocation,
                                        radius
                                    )
                                ) {
                                    // The location is inside this marker's circle
                                    Log.d(
                                        "Location Service",
                                        "Inside marker circle: ${enhancedMarker.properties.value.name}"
                                    )
                                    isInside = true
                                    markerName = enhancedMarker.properties.value.name
                                }
                            }
                        }

                        if(isInside){
                            familyDatabase.setChildStatus(
                                familyId,
                                memberId,
                                markerName
                            )
                        }else{
                            familyDatabase.setChildStatus(
                                familyId,
                                memberId,
                                null
                            )
                        }
                    }
                }
                .launchIn(serviceScope)
        }
        return START_NOT_STICKY
    }

    private fun sendInsideCircleNotification(circleName: String) {
        val notification = NotificationCompat.Builder(this, "SensorChannel")
            .setContentTitle("Inside Circle Alert")
            .setContentText("$memberId currently inside the circle: $circleName")
            .setSmallIcon(R.drawable.family) // replace with your icon
            .build()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(4, notification) // Use a unique ID, e.g., 4
    }

    private fun sendOutsideCircleNotification() {
        val notification = NotificationCompat.Builder(this, "SensorChannel")
            .setContentTitle("Outside Circle Alert")
            .setContentText("$memberId currently outside the circles")
            .setSmallIcon(R.drawable.family) // replace with your icon
            .build()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(4, notification) // Use a unique ID, e.g., 4
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

    private fun isLocationInsideCircle(location: LatLng, circleCenter: LatLng, radius: Float): Boolean {
        val earthRadius = 6371e3 // Earth's radius in meters

        val dLat = Math.toRadians(circleCenter.latitude - location.latitude)
        val dLon = Math.toRadians(circleCenter.longitude - location.longitude)

        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(location.latitude)) * cos(Math.toRadians(circleCenter.latitude)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        val distance = earthRadius * c // Distance in meters

        return distance <= radius
    }
}