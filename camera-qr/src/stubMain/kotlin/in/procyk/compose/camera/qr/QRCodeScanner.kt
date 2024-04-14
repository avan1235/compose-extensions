package `in`.procyk.compose.camera.qr

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color

@Composable
actual fun QRCodeScanner(
    onResult: (QRResult) -> Boolean,
    onIsLoadingChange: (Boolean) -> Unit,
    backgroundColor: Color,
    contentDescription: String?,
    missingCameraContent: @Composable () -> Unit,
) {
}
