package `in`.procyk.compose.camera.permission

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
actual fun rememberCameraPermissionState(): CameraPermissionState {
    val cameraPermissionState = rememberPermissionState(
        android.Manifest.permission.CAMERA,
    )
    return remember {
        object : CameraPermissionState {
            override val isAvailable: Boolean = true

            override val permission: CameraPermission
                get() = when (cameraPermissionState.status) {
                    PermissionStatus.Granted -> CameraPermission.Granted
                    is PermissionStatus.Denied -> CameraPermission.Denied
                }

            override fun launchRequest() = cameraPermissionState.launchPermissionRequest()
        }
    }
}
