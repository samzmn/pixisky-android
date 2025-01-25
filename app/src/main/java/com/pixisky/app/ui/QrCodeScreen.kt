package com.pixisky.app.ui

import android.content.pm.PackageManager
import android.graphics.RectF
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.core.graphics.toRectF
import com.google.mlkit.vision.barcode.common.Barcode
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pixisky.app.R
import com.pixisky.app.ui.navigation.NavigationDestination

object QrCodeDestination : NavigationDestination() {
    override val route = "QrCode"
    override val titleRes= R.string.qrcode_title
}

// Permission check and request
@Composable
fun QrCodeScreen(scanCode: (String) -> Unit) {
    val context = LocalContext.current
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED )
    }
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        hasCameraPermission = isGranted
    }

    if (hasCameraPermission) {
        CameraPreview(scanCode)
    } else {
        //requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Camera permission is required to scan QR codes.")
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { requestPermissionLauncher.launch(Manifest.permission.CAMERA) }
            ) {
                Text(text = "Grant Permission")
            }
        }
    }
}

// CameraPreview composable
//@SuppressLint("UnsafeOptInUsageError")
@OptIn(ExperimentalGetImage::class)
@Composable
fun CameraPreview(scanCode: (String) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }
    var detectedBarcodes by remember { mutableStateOf<List<Barcode>>(emptyList()) }
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
    val qrCodeScanner = BarcodeScanning.getClient()

    LaunchedEffect(cameraProviderFuture) {
        val cameraProvider = cameraProviderFuture.get()
        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }

        val imageAnalyzer = ImageAnalysis.Builder()
            .build()
            .also {
                it.setAnalyzer(ContextCompat.getMainExecutor(context)) { image ->
                    val mediaImage = image.image
                    if (mediaImage != null) {
                        val inputImage =
                            InputImage.fromMediaImage(mediaImage, image.imageInfo.rotationDegrees)
                        qrCodeScanner.process(inputImage)
                            .addOnSuccessListener { barcodes ->
                                detectedBarcodes = barcodes
                                barcodes.firstOrNull()?.rawValue?.let { url ->
                                    scanCode(url)
                                }
                            }
                            .addOnCompleteListener {
                                image.close()
                            }
                    } else {
                        image.close()
                    }
                }
            }


        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner, cameraSelector, preview, imageAnalyzer
            )
        } catch (exc: Exception) {
            Log.e("CameraPreview", "Use case binding failed", exc)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())
        BarcodeOverlay(detectedBarcodes)
    }
}

// Extension function to scale RectF
fun RectF.scale(factor: Float): RectF {
    return RectF(left * factor, top * factor, right * factor, bottom * factor)
}

// BarcodeOverlay composable to draw bounding boxes
@Composable fun BarcodeOverlay(barcodes: List<Barcode>) {
    val density = LocalDensity.current.density
    Canvas(modifier = Modifier.fillMaxSize()) {
        barcodes.forEach { barcode ->
            val rect = barcode.boundingBox?.toRectF()?.scale(density) ?: RectF()
            drawRect(
                color = Color.Yellow,
                topLeft = Offset(rect.left, rect.top),
                size = androidx.compose.ui.geometry.Size(rect.width(), rect.height()),
                style = Stroke(width = 4f) // Personalizing the stroke width
            )
        }
    }
}
