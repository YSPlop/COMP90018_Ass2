package com.example.safecircle.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

fun Context.hasAllPermissions(vararg permissions: String): Boolean {
    return permissions.all {
        ContextCompat.checkSelfPermission(this, it)  == PackageManager.PERMISSION_GRANTED
    }
}

fun Context.hasLocationPermissions(): Boolean = this.hasAllPermissions(
    Manifest.permission.ACCESS_COARSE_LOCATION,
    Manifest.permission.ACCESS_FINE_LOCATION,
)