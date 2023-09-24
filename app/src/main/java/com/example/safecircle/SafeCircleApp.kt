package com.example.safecircle

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context

class SafeCircleApp: Application() {
    override fun onCreate() {
        super.onCreate()
        val channel = NotificationChannel(
            "location_push_service",
            "Location",
            NotificationManager.IMPORTANCE_HIGH
        )
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}