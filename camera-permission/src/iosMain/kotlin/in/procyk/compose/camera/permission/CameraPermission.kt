package `in`.procyk.compose.camera.permission

import androidx.compose.runtime.*
import `in`.procyk.compose.camera.permission.CameraPermission.Denied
import `in`.procyk.compose.camera.permission.CameraPermission.Granted
import platform.AVFoundation.*
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSOperationQueue
import platform.UIKit.UIApplicationDidBecomeActiveNotification
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

@Composable
actual fun rememberCameraPermissionState(): CameraPermissionState {
    var cameraPermission by remember { mutableStateOf(currentCameraPermission()) }

    DisposableEffect(Unit) {
        cameraPermission = currentCameraPermission()
        val observer = NSNotificationCenter.defaultCenter.addObserverForName(
            name = UIApplicationDidBecomeActiveNotification,
            `object` = null,
            queue = NSOperationQueue.mainQueue,
        ) { _ -> cameraPermission = currentCameraPermission() }

        onDispose { NSNotificationCenter.defaultCenter.removeObserver(observer) }
    }

    return remember {
        object : CameraPermissionState {
            override val isAvailable: Boolean = true

            override val permission: CameraPermission get() = cameraPermission

            override fun launchRequest() {
                AVCaptureDevice.requestAccessForMediaType(mediaType = AVMediaTypeVideo) { success ->
                    dispatch_async(dispatch_get_main_queue()) {
                        cameraPermission = if (success) Granted else Denied
                    }
                }
            }
        }
    }
}

private fun currentCameraPermission(): CameraPermission =
    when (AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo)) {
        AVAuthorizationStatusAuthorized -> Granted
        else -> Denied
    }
