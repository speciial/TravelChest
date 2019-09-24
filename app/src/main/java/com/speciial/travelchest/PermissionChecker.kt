package com.speciial.travelchest

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object PermissionChecker {

    fun checkPermissions(activity: Activity){
        if ((Build.VERSION.SDK_INT >= 25 &&
                    ContextCompat.checkSelfPermission(
                        activity,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    ) !=
                    PackageManager.PERMISSION_GRANTED)
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION),
                0
            )
        }
    }

}