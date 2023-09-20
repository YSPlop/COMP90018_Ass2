package com.example.safecircle.services

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Icon
import androidx.core.app.NotificationCompat
import com.example.safecircle.R
import com.example.safecircle.db.FamilyLocationDao
import com.example.safecircle.interfaces.LocationClient
import com.example.safecircle.receivers.LocationServiceNotificationReceiver
import com.example.safecircle.utils.DefaultLocationClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlin.time.DurationUnit
import kotlin.time.toDuration


// Code adapted from Youtube
// https://www.youtube.com/watch?v=Jj14sw4Yxk0
class LocationPushService : Service() {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var familyId: String
    private lateinit var memberId: String
    private lateinit var locationDao: FamilyLocationDao
    private lateinit var locationClient: LocationClient
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        locationClient = DefaultLocationClient(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext),
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                familyId = intent.getStringExtra("familyId")!!
                memberId = intent.getStringExtra("memberId")!!
                locationDao = FamilyLocationDao.getInstance(familyId)
                start()
            }
            ACTION_STOP ->  stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun start() {
        val intent = Intent(applicationContext, LocationServiceNotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            3,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val action = Notification.Action.Builder(R.drawable.family, "Details", pendingIntent).build()
//        val notification = Notification.Builder(this, "location_push_service")
//            .setContentTitle("We are sharing you location")
//            .setSmallIcon(R.drawable.family)
//            .setOngoing(true)
//            .addAction(action)
        val notification = NotificationCompat.Builder(applicationContext, "location_push_service")
            .setContentTitle("Location Service")
            .setSmallIcon(R.drawable.family)
        locationClient.getLocationUpdates(10.toDuration(DurationUnit.SECONDS))
            .catch { e ->
                Log.e("Location Service", e.toString())
            }.onEach {
                Log.d("Location Service", "lat = ${it.latitude}, lng = ${it.longitude}")
                locationDao.updateCurrentMemberLocation(memberId, it.latitude, it.longitude)
            }.launchIn(serviceScope)
        startForeground(1, notification.build())

    }
    private fun stop() {
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
    }


}