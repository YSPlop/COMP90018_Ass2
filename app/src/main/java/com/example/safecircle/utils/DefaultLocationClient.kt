package com.example.safecircle.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import com.google.android.gms.location.LocationRequest
import com.example.safecircle.exceptions.LocationException
import com.example.safecircle.interfaces.LocationClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration

// Code from
// https://www.youtube.com/watch?v=Jj14sw4Yxk0
class DefaultLocationClient(
    private val context: Context,
    private val client: FusedLocationProviderClient,
): LocationClient {
    private fun buildLocationCallback(callback: (location: Location) -> Unit): LocationCallback {
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                super.onLocationResult(result)
                result.lastLocation?.let(callback)
            }
        }
        return locationCallback
    }
    @SuppressLint("MissingPermission")
    override fun getLocationUpdates(interval: Duration): Flow<Location> {
         return callbackFlow {
             // Check for Pre-conditions
             if (!context.hasLocationPermissions()) {
                 throw LocationException("No Location Permissions")
             }
             val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
             if (!locationManager.isLocationEnabled) {
                 throw  LocationException("Location not enabled")
             }
             val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
             val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
             if (!isGpsEnabled && !isNetworkEnabled) {
                 throw LocationException("Location provider not found")
             }

             val locationRequest = LocationRequest.Builder(interval.inWholeMilliseconds)
                 .build()
             val locationCallback = buildLocationCallback { location ->
                 launch { send(location) }
             }

             client.requestLocationUpdates(
                 locationRequest,
                 locationCallback,
                 Looper.getMainLooper()
             )

             awaitClose {
                 client.removeLocationUpdates(locationCallback)
             }
         }
    }

}