package `in`.procyk.compose.camera.permission

import androidx.compose.runtime.*
import `in`.procyk.compose.camera.permission.CameraPermission.Denied
import `in`.procyk.compose.camera.permission.CameraPermission.Granted

@Composable
actual fun rememberCameraPermissionState(): CameraPermissionState {
    var cameraPermission by remember { mutableStateOf(Denied) }

    DisposableEffect(Unit) {
        val observer = observeCameraPermission { granted -> cameraPermission = if (granted) Granted else Denied }
        onDispose { disposeCameraPermissionObserver(observer) }
    }

    return remember {
        object : CameraPermissionState {
            override val isAvailable: Boolean = isCameraApiAvailable()

            override val permission: CameraPermission get() = cameraPermission

            override fun launchRequest() {
                requestCameraAccess { granted -> cameraPermission = if (granted) Granted else Denied }
            }
        }
    }
}

private fun isCameraApiAvailable(): Boolean =
    js("!!(window.navigator.mediaDevices && window.navigator.mediaDevices.getUserMedia)")

/**
 * Reports the current camera permission and its further changes using the Permissions API.
 * When the API does not support querying `camera` (e.g. in some browsers),
 * falls back to checking whether labels of video input devices are exposed (which happens only after granting access).
 */
private fun observeCameraPermission(onChange: (Boolean) -> Unit): JsAny = js(
    """{
    const navigator = window.navigator;
    const observer = { disposed: false, status: null, listener: null };
    const report = (granted) => { if (!observer.disposed) onChange(granted); };
    const fallback = () => {
        if (!navigator.mediaDevices || !navigator.mediaDevices.enumerateDevices) return;
        navigator.mediaDevices.enumerateDevices()
            .then((devices) => report(devices.some((device) => device.kind === 'videoinput' && !!device.label)))
            .catch(() => {});
    };
    if (navigator.permissions && navigator.permissions.query) {
        navigator.permissions.query({ name: 'camera' })
            .then((status) => {
                if (observer.disposed) return;
                observer.status = status;
                observer.listener = () => report(status.state === 'granted');
                status.addEventListener('change', observer.listener);
                observer.listener();
            })
            .catch(fallback);
    } else {
        fallback();
    }
    return observer;
}"""
)

private fun disposeCameraPermissionObserver(observer: JsAny): Unit = js(
    """{
    observer.disposed = true;
    if (observer.status && observer.listener) observer.status.removeEventListener('change', observer.listener);
}"""
)

private fun requestCameraAccess(onResult: (Boolean) -> Unit): Unit = js(
    """{
    const mediaDevices = window.navigator.mediaDevices;
    if (!mediaDevices || !mediaDevices.getUserMedia) {
        onResult(false);
        return;
    }
    mediaDevices.getUserMedia({ video: true })
        .then((stream) => {
            stream.getTracks().forEach((track) => track.stop());
            onResult(true);
        })
        .catch(() => onResult(false));
}"""
)
