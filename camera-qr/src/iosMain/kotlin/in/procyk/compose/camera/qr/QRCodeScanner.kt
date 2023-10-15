package `in`.procyk.compose.camera.qr

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.interop.UIKitView
import `in`.procyk.compose.camera.OnceLaunchedEffect
import `in`.procyk.compose.camera.qr.CameraPermission.Denied
import `in`.procyk.compose.camera.qr.CameraPermission.Granted
import `in`.procyk.compose.camera.runIfNonNull
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCAction
import platform.AVFoundation.*
import platform.AVFoundation.AVCaptureDeviceDiscoverySession.Companion.discoverySessionWithDeviceTypes
import platform.AVFoundation.AVCaptureDeviceInput.Companion.deviceInputWithDevice
import platform.CoreGraphics.CGRect
import platform.Foundation.NSNotification
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSSelectorFromString
import platform.QuartzCore.CATransaction
import platform.QuartzCore.kCATransactionDisableActions
import platform.UIKit.UIDevice
import platform.UIKit.UIDeviceOrientation.*
import platform.UIKit.UIDeviceOrientationDidChangeNotification
import platform.UIKit.UIView
import platform.darwin.NSObject
import platform.darwin.dispatch_get_main_queue

@Composable
actual fun QRCodeScanner(
    onResult: (QRResult) -> Boolean,
    onIsLoadingChange: (Boolean) -> Unit,
    backgroundColor: Color,
    contentDescription: String?,
    missingCameraContent: @Composable () -> Unit,
) {
    val camera = remember {
        discoverySessionWithDeviceTypes(
            deviceTypes = DEVICE_TYPES,
            mediaType = AVMediaTypeVideo,
            position = AVCaptureDevicePositionBack,
        ).devices.firstOrNull() as? AVCaptureDevice
    }
    when {
        camera != null -> CameraView(camera, onResult, onIsLoadingChange, backgroundColor)
        else -> {
            OnceLaunchedEffect { onIsLoadingChange(false) }
            missingCameraContent()
        }
    }
}

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

private val DEVICE_TYPES: List<AVCaptureDeviceType> = listOf(
    AVCaptureDeviceTypeBuiltInWideAngleCamera,
    AVCaptureDeviceTypeBuiltInDualWideCamera,
    AVCaptureDeviceTypeBuiltInDualCamera,
    AVCaptureDeviceTypeBuiltInUltraWideCamera,
    AVCaptureDeviceTypeBuiltInDuoCamera,
)

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
@Composable
private fun CameraView(
    camera: AVCaptureDevice,
    onResult: (QRResult) -> Boolean,
    onIsLoadingChange: (Boolean) -> Unit,
    backgroundColor: Color,
) {
    val metadataOutput = remember { AVCaptureMetadataOutput() }
    var actualOrientation by remember { mutableStateOf(AVCaptureVideoOrientationPortrait) }
    var handleNext by remember { mutableStateOf(true) }

    val captureMetadataOutput = remember {
        object : NSObject(), AVCaptureMetadataOutputObjectsDelegateProtocol {
            override fun captureOutput(
                output: AVCaptureOutput,
                didOutputMetadataObjects: List<*>,
                fromConnection: AVCaptureConnection,
            ) {
                if (!handleNext) return

                val detected = QRResult.QRSuccess(didOutputMetadataObjects) {
                    (it as? AVMetadataMachineReadableCodeObject)?.stringValue
                }
                handleNext = runIfNonNull(detected, onResult) ?: return
            }
        }
    }
    val captureSession = remember {
        createCaptureSession(camera, metadataOutput, captureMetadataOutput).also { captureSession ->
            if (captureSession == null) onResult(QRResult.QRError)
        }
    } ?: return
    val cameraPreviewLayer = remember { AVCaptureVideoPreviewLayer(session = captureSession) }

    DisposableEffect(Unit) {
        fun updateCameraVideoOrientation() {
            val cameraConnection = cameraPreviewLayer.connection
            if (cameraConnection != null) {
                actualOrientation = when (UIDevice.currentDevice.orientation) {
                    UIDeviceOrientationPortrait -> AVCaptureVideoOrientationPortrait
                    UIDeviceOrientationLandscapeLeft -> AVCaptureVideoOrientationLandscapeRight
                    UIDeviceOrientationLandscapeRight -> AVCaptureVideoOrientationLandscapeLeft
                    UIDeviceOrientationPortraitUpsideDown -> AVCaptureVideoOrientationPortraitUpsideDown
                    else -> cameraConnection.videoOrientation
                }
                cameraConnection.videoOrientation = actualOrientation
            }
            metadataOutput
                .connectionWithMediaType(AVMediaTypeVideo)
                ?.videoOrientation = actualOrientation
        }

        class OrientationListener : NSObject() {
            @Suppress("UNUSED_PARAMETER")
            @ObjCAction
            fun orientationDidChange(arg: NSNotification) {
                updateCameraVideoOrientation()
            }
        }

        val listener = OrientationListener()
        NSNotificationCenter.defaultCenter.addObserver(
            observer = listener,
            selector = NSSelectorFromString(OrientationListener::orientationDidChange.name + ":"),
            name = UIDeviceOrientationDidChangeNotification,
            `object` = null
        )
        updateCameraVideoOrientation()

        onDispose {
            NSNotificationCenter.defaultCenter.removeObserver(
                observer = listener,
                name = UIDeviceOrientationDidChangeNotification,
                `object` = null
            )
        }
    }
    UIKitView(
        modifier = Modifier.fillMaxSize(),
        background = backgroundColor,
        factory = {
            val cameraContainer = UIView()
            cameraContainer.layer.addSublayer(cameraPreviewLayer)
            cameraPreviewLayer.videoGravity = AVLayerVideoGravityResizeAspectFill
            captureSession.startRunning()
            onIsLoadingChange(false)
            cameraContainer
        },
        onResize = { view: UIView, rect: CValue<CGRect> ->
            CATransaction.begin()
            CATransaction.setValue(true, kCATransactionDisableActions)
            view.layer.setFrame(rect)
            cameraPreviewLayer.setFrame(rect)
            CATransaction.commit()
        },
    )
}

@OptIn(ExperimentalForeignApi::class)
private fun createCaptureSession(
    camera: AVCaptureDevice,
    metadataOutput: AVCaptureMetadataOutput,
    captureMetadataOutput: AVCaptureMetadataOutputObjectsDelegateProtocol,
): AVCaptureSession? {
    val captureDeviceInput = deviceInputWithDevice(device = camera, error = null) ?: return null

    return AVCaptureSession().apply {
        if (!canAddInput(captureDeviceInput)) return null
        addInput(captureDeviceInput)

        if (!canAddOutput(metadataOutput)) return null
        addOutput(metadataOutput)
        metadataOutput.setMetadataObjectsDelegate(captureMetadataOutput, dispatch_get_main_queue())
        metadataOutput.setMetadataObjectTypes(listOf(AVMetadataObjectTypeQRCode))
    }
}
