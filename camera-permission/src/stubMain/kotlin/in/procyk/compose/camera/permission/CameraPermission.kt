package `in`.procyk.compose.camera.permission

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberCameraPermissionState(): CameraPermissionState = remember {
    UnavailableCameraPermissionState
}

private object UnavailableCameraPermissionState : CameraPermissionState {
    override val isAvailable: Boolean = false

    override val permission: CameraPermission = CameraPermission.Denied

    override fun launchRequest() = Unit
}