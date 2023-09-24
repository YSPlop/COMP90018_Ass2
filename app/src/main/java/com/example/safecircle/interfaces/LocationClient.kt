package com.example.safecircle.interfaces

import android.location.Location
import kotlin.time.Duration
import kotlinx.coroutines.flow.Flow

interface LocationClient {
    fun getLocationUpdates(interval: Duration): Flow<Location>
}