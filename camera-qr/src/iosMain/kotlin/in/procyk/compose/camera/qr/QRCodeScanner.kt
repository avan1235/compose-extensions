package `in`.procyk.compose.camera.qr

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.UIKitView
import `in`.procyk.compose.util.OnceLaunchedEffect
import `in`.procyk.compose.util.runIfNonNull
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFoundation.*
import platform.AVFoundation.AVCaptureDeviceDiscoverySession.Companion.discoverySessionWithDeviceTypes
import platform.AVFoundation.AVCaptureDeviceInput.Companion.deviceInputWithDevice
import platform.CoreGraphics.CGFloat
import platform.CoreGraphics.CGRectMake
import platform.Foundation.*
import platform.QuartzCore.CATransaction
import platform.UIKit.UIApplication
import platform.UIKit.UIColor
import platform.UIKit.UIDevice
import platform.UIKit.UIDeviceOrientation.*
import platform.UIKit.UIDeviceOrientationDidChangeNotification
import platform.UIKit.UIInterfaceOrientationLandscapeLeft
import platform.UIKit.UIInterfaceOrientationLandscapeRight
import platform.UIKit.UIInterfaceOrientationPortraitUpsideDown
import platform.UIKit.UIView
import platform.darwin.NSObject
import platform.darwin.NSObjectProtocol
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
import platform.darwin.dispatch_queue_create
import platform.darwin.dispatch_queue_t

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

private val DEVICE_TYPES: List<AVCaptureDeviceType> = listOf(
    AVCaptureDeviceTypeBuiltInWideAngleCamera,
    AVCaptureDeviceTypeBuiltInDualWideCamera,
    AVCaptureDeviceTypeBuiltInDualCamera,
    AVCaptureDeviceTypeBuiltInUltraWideCamera,
    AVCaptureDeviceTypeBuiltInDuoCamera,
)

@OptIn(ExperimentalForeignApi::class)
@Composable
private fun CameraView(
    camera: AVCaptureDevice,
    onResult: (QRResult) -> Boolean,
    onIsLoadingChange: (Boolean) -> Unit,
    backgroundColor: Color,
) {
    val currentOnResult by rememberUpdatedState(onResult)
    val currentOnIsLoadingChange by rememberUpdatedState(onIsLoadingChange)

    val metadataOutput = remember { AVCaptureMetadataOutput() }
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
                handleNext = runIfNonNull(detected, currentOnResult) ?: return
            }
        }
    }
    val captureSession = remember {
        createCaptureSession(camera, metadataOutput, captureMetadataOutput)
    }
    if (captureSession == null) {
        OnceLaunchedEffect {
            currentOnIsLoadingChange(false)
            currentOnResult(QRResult.QRError)
        }
        return
    }
    val cameraPreviewLayer = remember { AVCaptureVideoPreviewLayer(session = captureSession) }
    val sessionQueue = remember { dispatch_queue_create("in.procyk.compose.camera.qr.session", null) }

    val orientationObserver = remember(camera, cameraPreviewLayer) {
        OrientationObserver(camera, cameraPreviewLayer, metadataOutput)
    }
    DisposableEffect(orientationObserver) {
        onDispose { orientationObserver.dispose() }
    }

    DisposableEffect(captureSession) {
        sessionQueue.async {
            captureSession.startRunning()
            dispatch_async(dispatch_get_main_queue()) {
                orientationObserver.update()
                currentOnIsLoadingChange(false)
            }
        }
        onDispose {
            sessionQueue.async {
                if (captureSession.isRunning()) captureSession.stopRunning()
            }
        }
    }

    UIKitView(
        factory = {
            CameraPreviewView(cameraPreviewLayer, onLayoutChange = orientationObserver::update).apply {
                cameraPreviewLayer.videoGravity = AVLayerVideoGravityResizeAspectFill
            }
        },
        modifier = Modifier.fillMaxSize(),
        update = { view -> view.backgroundColor = backgroundColor.toUIColor() },
    )
}

@OptIn(ExperimentalForeignApi::class)
private class CameraPreviewView(
    private val previewLayer: AVCaptureVideoPreviewLayer,
    private val onLayoutChange: () -> Unit,
) : UIView(frame = CGRectMake(0.0, 0.0, 0.0, 0.0)) {

    init {
        layer.addSublayer(previewLayer)
    }

    override fun didMoveToWindow() {
        super.didMoveToWindow()
        // interface orientation is known only when attached to a window
        if (window != null) onLayoutChange()
    }

    override fun layoutSubviews() {
        super.layoutSubviews()
        CATransaction.begin()
        CATransaction.setDisableActions(true)
        previewLayer.setFrame(bounds)
        CATransaction.commit()
        onLayoutChange()
    }
}

/**
 * Keeps preview and metadata connections rotated according to the device orientation.
 * On iOS 17+ the angles are provided by [AVCaptureDeviceRotationCoordinator],
 * on older versions they are derived from [UIDevice] orientation.
 */
@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
private class OrientationObserver(
    camera: AVCaptureDevice,
    private val previewLayer: AVCaptureVideoPreviewLayer,
    private val metadataOutput: AVCaptureMetadataOutput,
) {
    private val device = UIDevice.currentDevice
    private val coordinator = when {
        NSClassFromString("AVCaptureDeviceRotationCoordinator") != null ->
            AVCaptureDeviceRotationCoordinator(device = camera, previewLayer = previewLayer)

        else -> null
    }
    private var disposed = false
    private val observer: NSObjectProtocol

    init {
        device.beginGeneratingDeviceOrientationNotifications()
        observer = NSNotificationCenter.defaultCenter.addObserverForName(
            name = UIDeviceOrientationDidChangeNotification,
            `object` = null,
            queue = NSOperationQueue.mainQueue,
        ) { _ ->
            // let the rotation coordinator process the orientation change first
            dispatch_async(dispatch_get_main_queue()) { update() }
        }
        update()
    }

    fun update() {
        if (disposed) return
        val previewAngle: CGFloat
        val captureAngle: CGFloat
        if (coordinator != null) {
            previewAngle = coordinator.videoRotationAngleForHorizonLevelPreview
            captureAngle = coordinator.videoRotationAngleForHorizonLevelCapture
        } else {
            previewAngle = when (device.orientation) {
                UIDeviceOrientationPortrait -> 90.0
                UIDeviceOrientationLandscapeLeft -> 0.0
                UIDeviceOrientationLandscapeRight -> 180.0
                UIDeviceOrientationPortraitUpsideDown -> 270.0
                // orientation unknown yet (e.g. right after start or with rotation lock), use interface orientation
                else -> when (UIApplication.sharedApplication.statusBarOrientation) {
                    UIInterfaceOrientationLandscapeLeft -> 180.0
                    UIInterfaceOrientationLandscapeRight -> 0.0
                    UIInterfaceOrientationPortraitUpsideDown -> 270.0
                    else -> 90.0
                }
            }
            captureAngle = previewAngle
        }
        previewLayer.connection?.applyRotationAngle(previewAngle)
        metadataOutput.connectionWithMediaType(AVMediaTypeVideo)?.applyRotationAngle(captureAngle)
    }

    fun dispose() {
        if (disposed) return
        disposed = true
        NSNotificationCenter.defaultCenter.removeObserver(observer)
        device.endGeneratingDeviceOrientationNotifications()
    }
}

@OptIn(ExperimentalForeignApi::class)
@Suppress("DEPRECATION")
private fun AVCaptureConnection.applyRotationAngle(angle: CGFloat) {
    if (respondsToSelector(NSSelectorFromString("setVideoRotationAngle:"))) {
        if (isVideoRotationAngleSupported(angle)) videoRotationAngle = angle
        return
    }
    if (!supportsVideoOrientation) return
    videoOrientation = when (angle) {
        0.0 -> AVCaptureVideoOrientationLandscapeRight
        180.0 -> AVCaptureVideoOrientationLandscapeLeft
        270.0 -> AVCaptureVideoOrientationPortraitUpsideDown
        else -> AVCaptureVideoOrientationPortrait
    }
}

private fun dispatch_queue_t.async(block: () -> Unit) {
    dispatch_async(this, block)
}

private fun Color.toUIColor(): UIColor = UIColor.colorWithRed(
    red = red.toDouble(),
    green = green.toDouble(),
    blue = blue.toDouble(),
    alpha = alpha.toDouble(),
)

@OptIn(ExperimentalForeignApi::class)
private fun createCaptureSession(
    camera: AVCaptureDevice,
    metadataOutput: AVCaptureMetadataOutput,
    captureMetadataOutput: AVCaptureMetadataOutputObjectsDelegateProtocol,
): AVCaptureSession? {
    val captureDeviceInput = deviceInputWithDevice(device = camera, error = null) ?: return null

    return AVCaptureSession().apply {
        beginConfiguration()
        try {
            if (!canAddInput(captureDeviceInput)) return null
            addInput(captureDeviceInput)

            if (!canAddOutput(metadataOutput)) return null
            addOutput(metadataOutput)
            metadataOutput.setMetadataObjectsDelegate(captureMetadataOutput, dispatch_get_main_queue())
            metadataOutput.setMetadataObjectTypes(listOf(AVMetadataObjectTypeQRCode))
        } finally {
            commitConfiguration()
        }
    }
}
