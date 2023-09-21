package com.example.safecircle.sensors

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager

class BatterySensorManager(context: Context, private val callback: (Float) -> Unit) {
    private val batteryStatusReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val level: Int = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
            val scale: Int = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
            val batteryPct: Float = level / scale.toFloat() * 100
            callback(batteryPct)
        }
    }

    private val mContext: Context = context.applicationContext

    fun registerListener() {
        val batteryStatusFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        mContext.registerReceiver(batteryStatusReceiver, batteryStatusFilter)
    }

    fun unregisterListener() {
        mContext.unregisterReceiver(batteryStatusReceiver)
    }
}