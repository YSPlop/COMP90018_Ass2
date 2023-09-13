package com.example.safecircle

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class TemperatureSensorManager(
    private val context: Context,
    private val callback: (Boolean, Float) -> Unit
) : SensorEventListener {

    private val sensorManager: SensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val temperatureSensor: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)

    init {
        if (temperatureSensor != null) {
            sensorManager.registerListener(
                this,
                temperatureSensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
            callback(true, 0.0f) // Notify the initial state
        } else {
            callback(false, 0.0f)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // Do something here if sensor accuracy changes.
    }

    override fun onSensorChanged(event: SensorEvent) {
        val temperatureValue = event.values[0]
        callback(true, temperatureValue)
    }

    fun unregisterListener() {
        sensorManager.unregisterListener(this)
    }
}
