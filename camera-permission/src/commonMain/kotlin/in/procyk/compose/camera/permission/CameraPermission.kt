package `in`.procyk.compose.camera.permission

import androidx.compose.runtime.Composable

@Composable
expect fun rememberCameraPermissionState(): CameraPermissionState

interface CameraPermissionState {
    val isAvailable: Boolean

    val permission: CameraPermission

    fun launchRequest()
}

enum class CameraPermission {
    Granted, Denied;

    val isGranted: Boolean get() = this == Granted
}
