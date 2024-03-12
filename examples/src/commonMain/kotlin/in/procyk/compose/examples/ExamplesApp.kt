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

    var selectedExample by remember { mutableStateOf<Example?>(null) }

    ExampleSystemBarsScreen(isCloseAvailable = false) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            for (example in Example.entries) {
                ExampleButton(example.presentableName) { selectedExample = example }
            }
        }
    }
    when (selectedExample) {
        Example.Calendar -> Calendars { selectedExample = null }
        Example.CameraPermission -> CameraPermission(cameraPermissionState) { selectedExample = null }
        Example.CameraQR -> CameraQR(cameraPermissionState) { selectedExample = null }
        Example.QRCode -> QRCode { selectedExample = null }
        null -> {}
    }
}

private enum class Example(val presentableName: String) {
    Calendar("calendar"),
    CameraPermission("camera-permission"),
    CameraQR("camera-qr"),
    QRCode("qr-code"),
    ;
}
