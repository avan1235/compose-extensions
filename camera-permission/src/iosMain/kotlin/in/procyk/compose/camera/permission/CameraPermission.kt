package `in`.procyk.compose.camera.permission

import androidx.compose.runtime.*
import `in`.procyk.compose.camera.permission.CameraPermission.Denied
import `in`.procyk.compose.camera.permission.CameraPermission.Granted
import `in`.procyk.compose.util.OnceLaunchedEffect
import platform.AVFoundation.*

@Composable
actual fun rememberCameraPermissionState(): CameraPermissionState {
    var cameraPermission by remember { mutableStateOf(Denied) }

    OnceLaunchedEffect {
        cameraPermission = when (AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo)) {
            AVAuthorizationStatusAuthorized -> Granted
            AVAuthorizationStatusDenied,
            AVAuthorizationStatusRestricted,
            AVAuthorizationStatusNotDetermined,
            -> Denied

            else -> error("Unexpected AVAuthorizationStatus")
        }
    }

    return remember {
        object : CameraPermissionState {
            override val isAvailable: Boolean = true

            override val permission: CameraPermission get() = cameraPermission

            override fun launchRequest() {
                AVCaptureDevice.requestAccessForMediaType(mediaType = AVMediaTypeVideo) { success ->
                    cameraPermission = if (success) Granted else Denied
                }
            }
        }
    }
}
