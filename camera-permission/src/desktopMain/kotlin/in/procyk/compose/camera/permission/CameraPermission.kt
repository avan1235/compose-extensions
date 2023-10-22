package `in`.procyk.compose.camera.permission

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.github.eduramiba.webcamcapture.drivers.NativeDriver
import com.github.sarxos.webcam.Webcam
import com.github.sarxos.webcam.WebcamException
import `in`.procyk.compose.util.runIfNonNull

@Composable
actual fun rememberCameraPermissionState(): CameraPermissionState = remember {
    object : CameraPermissionState {
        init {
            val os = System.getProperty("os.name")
            if (os.contains("mac", ignoreCase = true)) {
                Webcam.setDriver(NativeDriver())
            }
        }

        override val isAvailable: Boolean by lazy {
            try {
                runIfNonNull(Webcam.getDefault()) { true } ?: false
            } catch (_: WebcamException) {
                false
            }
        }

        override val permission: CameraPermission
            get() = if (isAvailable) CameraPermission.Granted else CameraPermission.Denied

        override fun launchRequest() = Unit
    }
}
