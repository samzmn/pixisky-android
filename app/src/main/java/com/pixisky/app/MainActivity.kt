package com.pixisky.app

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.pixisky.app.ui.theme.PixiSkyTheme

class MainActivity : ComponentActivity() {

    private lateinit var permissionManager: PermissionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        permissionManager = PermissionManager(this)
        setContent {
            PixiSkyTheme {
                var showDialog by remember { mutableStateOf(false) }
                var deniedPermission by remember { mutableStateOf("") }
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    PixiSkyApp(
                        permissionManager = permissionManager,
                        onPermissionDenied = { permission ->
                            deniedPermission = permission
                            showDialog = true
                        }
                    )
                    if (showDialog) {
                        ShowPermissionDeniedDialog(
                            permission = deniedPermission,
                            onDismiss = { showDialog = false },
                            onOpenSettings = { openAppSettings() }
                        )
                    }
                }
            }
        }
        checkPermissions()
    }

    private fun checkPermissions() {
        if (!permissionManager.hasCameraPermission()) {
            permissionManager.requestCameraPermission(this)
        }

        if (!permissionManager.hasStoragePermission()) {
            permissionManager.requestStoragePermission(this)
        }

        if (!permissionManager.hasInternetPermission()) {
            permissionManager.requestInternetPermission(this)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)
        when (requestCode) {
            PermissionManager.REQUEST_CAMERA_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Camera Permission Granted", Toast.LENGTH_SHORT).show()
                } else {
                    setContent {
                        PixiSkyTheme {
                            PixiSkyApp(permissionManager = permissionManager, onPermissionDenied = { "Camera" })
                        }
                    }
                }
            }
            PermissionManager.REQUEST_STORAGE_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    Toast.makeText(this, "Storage Permission Granted", Toast.LENGTH_SHORT).show()
                } else {
                    setContent {
                        PixiSkyTheme {
                            PixiSkyApp(permissionManager = permissionManager, onPermissionDenied = { "Storage" })
                        }
                    }
                }
            }
            PermissionManager.REQUEST_INTERNET_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Internet Permission Granted", Toast.LENGTH_SHORT).show()
                } else {
                    setContent {
                        PixiSkyTheme {
                            PixiSkyApp(permissionManager = permissionManager, onPermissionDenied = { "Internet" })
                        }
                    }
                }
            }
        }
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }
        startActivity(intent)
    }
}

@Composable
fun ShowPermissionDeniedDialog(permission: String, onDismiss: () -> Unit, onOpenSettings: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "$permission Permission Denied") },
        text = { Text(text = "This app requires $permission permission to function properly. Please grant the permission in the app settings.") },
        confirmButton = {
            Button(
                onClick = {
                    onOpenSettings()
                    onDismiss()
                }
            ) {
                Text(text = "Open Settings")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss
            ) {
                Text(text = "Cancel")
            }
        }
    )
}