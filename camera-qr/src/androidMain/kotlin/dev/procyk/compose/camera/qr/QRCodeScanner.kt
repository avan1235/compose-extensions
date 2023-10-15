package dev.procyk.compose.camera.qr

import android.media.Image
import android.widget.LinearLayout.LayoutParams
import android.widget.ListPopupWindow
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Composable
actual fun QRCodeScanner(
    onResult: (QRResult) -> Boolean,
    onIsLoadingChange: (Boolean) -> Unit,
    backgroundColor: Color,
    contentDescription: String?,
    missingCameraContent: @Composable () -> Unit,
) {
    val localContext = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val analysisExecutor = rememberExecutor()
    val cameraController = remember { LifecycleCameraController(localContext) }
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            PreviewView(context).apply {
                setBackgroundColor(backgroundColor.toArgb())

                layoutParams = LayoutParams(ListPopupWindow.MATCH_PARENT, ListPopupWindow.MATCH_PARENT)
                scaleType = PreviewView.ScaleType.FILL_START
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                controller = cameraController

                var handleNext = true

                cameraController.setImageAnalysisAnalyzer(
                    analysisExecutor,
                    QRCodeAnalyzer(
                        handle = { handleNext = handleNext && onResult(it) },
                        onPassCompleted = onIsLoadingChange,
                    )
                )

                cameraController.bindToLifecycle(lifecycleOwner)
            }
        },
        onRelease = {
            cameraController.unbind()
            analysisExecutor.shutdown()
        }
    )
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
actual fun rememberCameraPermissionState(): CameraPermissionState {
    val cameraPermissionState = rememberPermissionState(
        android.Manifest.permission.CAMERA
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

@Composable
private fun rememberExecutor(): ExecutorService {
    val executor = remember { Executors.newSingleThreadExecutor() }
    DisposableEffect(Unit) {
        onDispose {
            executor.shutdown()
        }
    }
    return executor
}

internal class QRCodeAnalyzer(
    private val handle: (QRResult) -> Unit,
    private val onPassCompleted: (failureOccurred: Boolean) -> Unit,
) : ImageAnalysis.Analyzer {

    private val barcodeScanner: BarcodeScanner? by lazy {
        try {
            BarcodeScanning.getClient(
                BarcodeScannerOptions.Builder()
                    .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                    .build()
            )
        } catch (_: Exception) {
            handleSynchronized(QRResult.QRError)
            null
        }
    }

    @Volatile
    private var failureTimestamp: Long = NO_FAILURE_FLAG

    @ExperimentalGetImage
    override fun analyze(imageProxy: ImageProxy) {
        val image = imageProxy.image ?: return

        if (failureTimestamp.isFailure && System.currentTimeMillis() - failureTimestamp < FAILURE_THROTTLE_MILLIS) {
            return imageProxy.close()
        }
        failureTimestamp = NO_FAILURE_FLAG

        val scanner = barcodeScanner ?: return
        val rotationDegrees = imageProxy.imageInfo.rotationDegrees
        scanner
            .process(image.toInputImage(rotationDegrees))
            .addOnSuccessListener { onNonEmptySuccess(it) }
            .addOnFailureListener {
                failureTimestamp = System.currentTimeMillis()
                handleSynchronized(QRResult.QRError)
            }
            .addOnCompleteListener {
                onPassCompleted(failureTimestamp.isFailure)
                imageProxy.close()
            }
    }

    @Synchronized
    private fun handleSynchronized(result: QRResult) {
        handle(result)
    }

    private fun onNonEmptySuccess(codes: List<Barcode?>?) {
        val qrResult = QRResult.QRSuccess(codes) { it?.rawValue } ?: return
        handleSynchronized(qrResult)
    }
}

private const val FAILURE_THROTTLE_MILLIS: Long = 1_000L

private const val NO_FAILURE_FLAG: Long = Long.MIN_VALUE

private inline val Long.isFailure: Boolean get() = this != NO_FAILURE_FLAG

@ExperimentalGetImage
private fun Image.toInputImage(rotationDegrees: Int): InputImage =
    InputImage.fromMediaImage(this, rotationDegrees)
