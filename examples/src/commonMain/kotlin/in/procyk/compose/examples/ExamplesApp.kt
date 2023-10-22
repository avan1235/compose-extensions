package `in`.procyk.compose.examples

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import `in`.procyk.compose.camera.permission.rememberCameraPermissionState

@Composable
internal fun ExamplesApp() = MaterialTheme {
    val cameraPermissionState = rememberCameraPermissionState()

    var visibleCameraPermission by remember { mutableStateOf(false) }
    var visibleCameraQR by remember { mutableStateOf(false) }

    ExampleSystemBarsScreen(isCloseAvailable = false) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ExampleButton("camera-permission") { visibleCameraPermission = true }
            ExampleButton("camera-qr") { visibleCameraQR = true }
        }
    }
    CameraPermission(cameraPermissionState, visibleCameraPermission) { visibleCameraPermission = it }
    CameraQR(cameraPermissionState, visibleCameraQR) { visibleCameraQR = it }
}