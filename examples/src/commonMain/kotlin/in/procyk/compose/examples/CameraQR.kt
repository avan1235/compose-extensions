package `in`.procyk.compose.examples

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import `in`.procyk.compose.camera.permission.CameraPermissionState
import `in`.procyk.compose.camera.qr.QRCodeScanner
import `in`.procyk.compose.camera.qr.QRResult

@Composable
internal fun CameraQR(
    cameraPermissionState: CameraPermissionState,
    visible: Boolean,
    onVisibleChange: (Boolean) -> Unit,
) = ExampleNoSystemBarsScreen(visible, onVisibleChange) {
    when {
        !cameraPermissionState.isAvailable -> Text("Camera not available")
        !cameraPermissionState.permission.isGranted -> Text("Missing camera permission")
        cameraPermissionState.permission.isGranted -> ScanQRCodes()
    }
}

@Composable
private fun ScanQRCodes() {
    var result by remember { mutableStateOf<QRResult?>(null) }
    var isCameraLoading by remember { mutableStateOf(true) }
    when (val codes = result) {
        is QRResult.QRSuccess -> Text("Scanned: ${codes.nonEmptyCodes}")
        else -> QRCodeScanner(
            onResult = { currentResult ->
                result = currentResult
                currentResult is QRResult.QRSuccess
            },
            onIsLoadingChange = { isCameraLoading = it },
            backgroundColor = MaterialTheme.colorScheme.background,
        )
    }
    AnimatedVisibility(
        visible = isCameraLoading,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}