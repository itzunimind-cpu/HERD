package com.motisoft.herd.ui.screens.scan

import android.Manifest
import android.content.pm.PackageManager
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.motisoft.herd.ui.components.PrimaryButton
import com.motisoft.herd.ui.theme.CameraBackground
import com.motisoft.herd.ui.theme.Cream
import com.motisoft.herd.ui.theme.Sage

// Viewfinder-only mock, matching the static design: no OCR/RFID decode logic wired
// up yet (explicitly out of scope for v1, see PROJECT_HANDOFF.md open questions).
@Composable
fun ScanTagScreen(onClose: () -> Unit) {
    val context = LocalContext.current
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED,
        )
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted -> hasPermission = granted }

    Box(modifier = Modifier.fillMaxSize().background(CameraBackground)) {
        if (hasPermission) {
            CameraPreview()
            ViewfinderOverlay()
        } else {
            PermissionRequest(onRequest = { permissionLauncher.launch(Manifest.permission.CAMERA) })
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(top = 12.dp, start = 4.dp, end = 4.dp),
            contentAlignment = Alignment.Center,
        ) {
            IconButton(onClick = onClose, modifier = Modifier.align(Alignment.CenterStart)) {
                Icon(Icons.Default.Close, contentDescription = "बंद करा", tint = Cream)
            }
            Text("टॅग स्कॅन करा", color = Cream, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
private fun CameraPreview() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.surfaceProvider = previewView.surfaceProvider
                }
                runCatching {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview)
                }
            }, ContextCompat.getMainExecutor(ctx))
            previewView
        },
    )
}

// Four corner brackets only, not a full frame - matches the design spec exactly.
@Composable
private fun ViewfinderOverlay() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(240.dp)) {
            val strokeWidth = 5.dp.toPx()
            val cornerLength = 36.dp.toPx()
            val w = size.width
            val h = size.height

            // top-left
            drawLine(Sage, Offset(0f, 0f), Offset(cornerLength, 0f), strokeWidth)
            drawLine(Sage, Offset(0f, 0f), Offset(0f, cornerLength), strokeWidth)
            // top-right
            drawLine(Sage, Offset(w, 0f), Offset(w - cornerLength, 0f), strokeWidth)
            drawLine(Sage, Offset(w, 0f), Offset(w, cornerLength), strokeWidth)
            // bottom-left
            drawLine(Sage, Offset(0f, h), Offset(cornerLength, h), strokeWidth)
            drawLine(Sage, Offset(0f, h), Offset(0f, h - cornerLength), strokeWidth)
            // bottom-right
            drawLine(Sage, Offset(w, h), Offset(w - cornerLength, h), strokeWidth)
            drawLine(Sage, Offset(w, h), Offset(w, h - cornerLength), strokeWidth)
        }
        Text(
            "गाईचा टॅग चौकटीत ठेवा",
            color = Cream,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 64.dp),
        )
    }
}

@Composable
private fun PermissionRequest(onRequest: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
    ) {
        Text(
            "टॅग स्कॅन करण्यासाठी कॅमेरा वापरण्याची परवानगी द्या",
            color = Cream,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 24.dp),
        )
        PrimaryButton(text = "परवानगी द्या", onClick = onRequest)
    }
}
