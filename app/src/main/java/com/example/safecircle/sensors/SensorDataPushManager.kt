package com.example.safecircle.sensors

import android.util.Log
import com.example.safecircle.database.FamilyDatabase
import com.example.safecircle.database.Role
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Pushes realtime sensor data to database at a set interval.
 */
class SensorDataPushManager(private val sensorService: ForegroundSensorService) {

    var interval: Long = 10000

    private var hasUser: Boolean = false
    private var familyId: String? = null
    private var username: String? = null
    private var userRole: Role? = null
    private var job: Job? = null

    private var savedTemperature: Float = 256f;
    private var savedBattery: Float = -1f;

    fun start() {
        job = CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                Log.i("SensorDataPushManager", "Coroutine update.")
                pushData();
                delay(interval)
            }
        }
    }

    fun stop() {
        job?.cancel()
    }

    /**
     * Push child sensor data to remote database.
     */
    private fun pushData() {
        if (!hasUser || FamilyDatabase.getInstance() == null) return
        if (userRole != Role.CHILD) {
            Log.i(
                "SensorDataPushManager",
                "User role is not child, will not push sensor data to remote database."
            )
            return
        };

        val db = FamilyDatabase.getInstance()
        Log.i("SensorDataPushManager", "Get database.")

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

    /**
     *  Sets the target user for updating sensor information to database.
     */
    fun setUser(familyId: String, username: String, userRole: Role) {
        this.hasUser = true
        this.familyId = familyId
        this.username = username
        this.userRole = userRole
        Log.i("SensorDataPushManager", "Set user: " + username)
    }

    /**
     * Unsets the target user for updating sensor information to database.
     * Sensor information will not be pushed to database when there is no user set.
     */
    fun unsetUser() {
        this.hasUser = false
        this.familyId = null
        this.username = null
        this.userRole = null
        Log.i("SensorDataPushManager", "Unset user.")
    }
}