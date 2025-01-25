package com.pixisky.app
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionManager(private val context: Context) {

    companion object {
        const val REQUEST_CAMERA_PERMISSION = 100
        const val REQUEST_STORAGE_PERMISSION = 101
        const val REQUEST_INTERNET_PERMISSION = 102
    }

    private fun hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission(activity: Activity, permission: String, requestCode: Int) {
        ActivityCompat.requestPermissions(activity, arrayOf(permission), requestCode)
    }

    fun hasCameraPermission(): Boolean {
        return hasPermission(android.Manifest.permission.CAMERA)
    }

    fun requestCameraPermission(activity: Activity) {
        requestPermission(activity, android.Manifest.permission.CAMERA, REQUEST_CAMERA_PERMISSION)
    }

    fun hasStoragePermission(): Boolean {
        return hasPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    fun requestStoragePermission(activity: Activity) {
        requestPermission(activity, android.Manifest.permission.READ_EXTERNAL_STORAGE, REQUEST_STORAGE_PERMISSION)
    }

    fun hasInternetPermission(): Boolean {
        return hasPermission(android.Manifest.permission.INTERNET)
    }

    fun requestInternetPermission(activity: Activity) {
        requestPermission(activity, android.Manifest.permission.INTERNET, REQUEST_INTERNET_PERMISSION)
    }
}
