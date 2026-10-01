package `in`.procyk.compose.examples

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import `in`.procyk.compose.camera.permission.CameraPermissionState

@Composable
internal fun CameraPermission(
    cameraPermissionState: CameraPermissionState,
    onClose: () -> Unit,
) = ExampleSystemBarsScreen(onClose) {
    when {
        !cameraPermissionState.isAvailable -> Text("Camera not available")
        cameraPermissionState.permission.isGranted -> Text("Permission granted")
        else -> Button(onClick = cameraPermissionState::launchRequest) {
            Text("Request camera permission")
        }
    }
}
