package com.example.safecircle.sensors

import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import com.example.safecircle.R
import com.example.safecircle.database.FamilyDatabase
import com.example.safecircle.database.Role
import com.example.safecircle.ui.screen.PersonInfo
import com.example.safecircle.utils.GlobalState
import com.example.safecircle.utils.PreferenceHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Pushes realtime sensor data to database at a set interval.
 */
class SensorDataPushManager(private val context: Context, private val sensorService: ForegroundSensorService) {

    companion object {
        // Static counter for instances
        private var instanceCount = 0
        // Collection of active instances
        private val activeInstances = mutableSetOf<SensorDataPushManager>()

        // Static function to log active instances and their roles
        fun logActiveInstances() {
            Log.i("InstanceTracking", "Total active instances: $instanceCount")
            activeInstances.forEach {
                Log.i("InstanceTracking", "Instance role: ${it.userRole}")
            }
        }
    }

    init {
        // Update the static variables when a new instance is created
        instanceCount++
        activeInstances.add(this)
        logActiveInstances()
    }

    // Finalizer to update static variables when the instance is garbage collected
    @Throws(Throwable::class)
    protected fun finalize() {
        instanceCount--
        activeInstances.remove(this)
    }




    var interval: Long = 10000
    val preferenceHelper = PreferenceHelper(context)

//    private var hasUser: Boolean = true
    private var familyId: String? = preferenceHelper.getFamilyID()
    private var username: String? = preferenceHelper.getUsername()
    private var userRole: Role? = preferenceHelper.getRole()
    private var job: Job? = null
    private val previousIsInsideStatus: MutableMap<String, String> = mutableMapOf()


    private var savedTemperature: Float = 256f;
    private var savedBattery: Float = -1f;

    //notification time stamp
    private var lastBatteryNotificationTime: Long = 0
    private var lastTemperatureNotificationTime: Long = 0


    // Cooldown period (e.g., 15 minutes)
    private val NOTIFICATION_COOLDOWN = 15 * 60 * 1000

    fun start() {
        job = CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                Log.i("SensorDataPushManager", "Coroutine update, role: $userRole")
                pullData()
                pushData();
                delay(interval)
            }
        }
    }

    fun stop() {
        job?.cancel()
    }

    fun cleanup() {
        // Stop the running coroutine job, if any.
        stop()

        // You can also add logic here to release any other resources or listeners
        // that your `SensorDataPushManager` might be using.
        // ...

        // Update the static tracking if you want to track cleanup:
        instanceCount--
        activeInstances.remove(this)
    }

    /**
     * Fetch child sensor data to from database.
     */
    private fun pullData() {
        if (userRole != Role.PARENT) {
            Log.i(
                "SensorDataPushManager",
                "User role is not parent, will not pull sensor data from remote database."
            )
            return
        }
        val db = FamilyDatabase.getInstance()
        db.getAllChildrenInfo(familyId!!) {children ->
//            GlobalState.childList.forEach{person ->
//                previousIsInsideStatus[person.name] = person.isInside
//            }
            // Logging each person's temperature and phoneBattery
            children.forEach { person ->
                Log.i("SensorDataPushManager", "Name: ${person.name}, Temperature: ${person.temperature}, Battery: ${person.phoneBattery}")
                if (person.phoneBattery.toFloat() <= 20.0 && canSendBatteryNotification()) {
                    sendBatteryLowNotification(person.name)
                    lastBatteryNotificationTime = System.currentTimeMillis()
                }
                if (person.temperature.toFloat() >= 40.0 && canSendTemperatureNotification()) {
                    sendTemperatureHighNotification(person.name)
                    lastTemperatureNotificationTime = System.currentTimeMillis()
                }
//                val previousStatus = previousIsInsideStatus[person.name]
//                if(person.isInside != previousStatus) {
                    if (person.isInside == "true") {
                        sendInsideNotification(person.name, person.markerName)
                    }
                    else {
                        sendOutsideNotification(person.name)
                    }
//                    // Update the map with the current status
//                    previousIsInsideStatus[person.name] = person.isInside
//                }
            }
            GlobalState.childList = children
        }
    }


    /**
     * Push child sensor data to remote database.
     */
    private fun pushData() {
//        if (!hasUser || FamilyDatabase.getInstance() == null) return
        if (userRole != Role.CHILD) {
            Log.i(
                "SensorDataPushManager",
                "User role is not child, will not push sensor data to remote database."
            )
            return
        };

        val db = FamilyDatabase.getInstance()
        Log.i("SensorDataPushManager", "Get database.")

        Log.i("SensorDataPushManager", "familyid: $familyId, username: $username, temp: ${sensorService.temperatureValue}, battery: ${sensorService.batteryValue}")
        // Push child temperature to database.
        if (sensorService.isTemperatureSensorAvailable
            && sensorService.temperatureValue != savedTemperature
        ) {
            db.setChildTemperature(
                familyId,
                username,
                sensorService.temperatureValue
            )
        }

        // Push child battery to database.
        if (sensorService.isBatterySensorAvailable
            && sensorService.batteryValue != savedBattery
        ) {
            db.setChildBattery(
                familyId, username, sensorService.batteryValue
            )
        }

    }

    // helper function for notification sending
    private fun canSendBatteryNotification(): Boolean {
        return (System.currentTimeMillis() - lastBatteryNotificationTime) > NOTIFICATION_COOLDOWN
    }

    private fun canSendTemperatureNotification(): Boolean {
        return (System.currentTimeMillis() - lastTemperatureNotificationTime) > NOTIFICATION_COOLDOWN
    }
    private fun sendBatteryLowNotification(name: String) {
        val notification = NotificationCompat.Builder(context, "SensorChannel")
            .setContentTitle("Battery Low")
            .setContentText("Battery level for $name is below 20%")
            .setSmallIcon(R.drawable.family) // replace with your icon
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(2, notification)
    }

    private fun sendTemperatureHighNotification(name: String) {
        val notification = NotificationCompat.Builder(context, "SensorChannel")
            .setContentTitle("High Temperature")
            .setContentText("Detecting high temperature for $name")
            .setSmallIcon(R.drawable.family) // replace with your icon
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(4, notification)
    }

    private fun sendInsideNotification(name: String, markerName: String?) {
        val notification = NotificationCompat.Builder(context, "SensorChannel")
            .setContentTitle("Location Status")
            .setContentText("$name is inside Circle: $markerName")
            .setSmallIcon(R.drawable.family) // replace with your icon
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(5, notification)
    }

    private fun sendOutsideNotification(name: String) {
        val notification = NotificationCompat.Builder(context, "SensorChannel")
            .setContentTitle("Location Status")
            .setContentText("$name is outside of circles")
            .setSmallIcon(R.drawable.family) // replace with your icon
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(5, notification)
    }
}