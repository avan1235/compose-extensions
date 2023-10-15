package dev.procyk.compose.camera.qr

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
expect fun QRCodeScanner(
    onResult: (QRResult) -> Boolean,
    onIsLoadingChange: (Boolean) -> Unit,
    backgroundColor: Color,
    contentDescription: String? = null,
    missingCameraContent: @Composable () -> Unit = {},
)

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

sealed interface QRResult {

    class QRSuccess private constructor(val nonEmptyCodes: List<String>) : QRResult {
        companion object {
            operator fun <T : Any> invoke(candidates: List<T?>?, f: (T?) -> String?): QRSuccess? {
                val nonNullCandidates = candidates ?: return null
                val someCandidates = nonNullCandidates.mapNotNull(f).takeIf { it.isNotEmpty() } ?: return null
                return QRSuccess(someCandidates)
            }

            operator fun <T : Any> invoke(candidates: Array<T?>?, f: (T?) -> String?): QRSuccess? {
                val nonNullCandidates = candidates ?: return null
                val someCandidates = nonNullCandidates.mapNotNull(f).takeIf { it.isNotEmpty() } ?: return null
                return QRSuccess(someCandidates)
            }
        }
    }

    data object QRError : QRResult
}