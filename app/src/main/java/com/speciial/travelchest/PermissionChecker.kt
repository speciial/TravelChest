package com.speciial.travelchest

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object PermissionChecker {

    fun checkPermissions(activity: Activity){
        if (
            (ContextCompat.checkSelfPermission(
                        activity,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    ) !=
                    PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                activity,
                android.Manifest.permission.CAMERA
            ) !=
                    PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                activity,
                android.Manifest.permission.RECORD_AUDIO
            ) !=
                    PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                activity,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) !=
                    PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                activity,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) !=
                    PackageManager.PERMISSION_GRANTED)
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.CAMERA, android.Manifest.permission.RECORD_AUDIO,android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                0
            )
        }
    }


}