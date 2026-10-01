package `in`.procyk.compose.camera.qr

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.WebElementView
import `in`.procyk.compose.util.OnceLaunchedEffect
import kotlinx.browser.document
import kotlinx.coroutines.suspendCancellableCoroutine
import org.w3c.dom.HTMLDivElement
import kotlin.coroutines.resume
import kotlin.math.roundToInt

@Composable
actual fun QRCodeScanner(
    onResult: (QRResult) -> Boolean,
    onIsLoadingChange: (Boolean) -> Unit,
    backgroundColor: Color,
    contentDescription: String?,
    missingCameraContent: @Composable () -> Unit,
) {
    val currentOnResult by rememberUpdatedState(onResult)
    val currentOnIsLoadingChange by rememberUpdatedState(onIsLoadingChange)
    var scannerState by remember { mutableStateOf(ScannerState.Initializing) }

    OnceLaunchedEffect {
        currentOnIsLoadingChange(true)
        scannerState = when {
            !awaitVideoInputPresence() -> ScannerState.MissingCamera
            !awaitHtml5QrcodeLibrary() -> ScannerState.Failed
            else -> ScannerState.Ready
        }
        when (scannerState) {
            ScannerState.MissingCamera -> currentOnIsLoadingChange(false)
            ScannerState.Failed -> {
                currentOnIsLoadingChange(false)
                currentOnResult(QRResult.QRError)
            }

            else -> Unit
        }
    }

    when (scannerState) {
        ScannerState.Ready -> CameraView(currentOnResult, currentOnIsLoadingChange, backgroundColor, contentDescription)
        ScannerState.MissingCamera -> missingCameraContent()
        ScannerState.Initializing, ScannerState.Failed -> Unit
    }
}

private enum class ScannerState { Initializing, MissingCamera, Failed, Ready }

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun CameraView(
    onResult: (QRResult) -> Boolean,
    onIsLoadingChange: (Boolean) -> Unit,
    backgroundColor: Color,
    contentDescription: String?,
) {
    val currentOnResult by rememberUpdatedState(onResult)
    val currentOnIsLoadingChange by rememberUpdatedState(onIsLoadingChange)
    val elementId = remember { "in-procyk-compose-camera-qr-${nextScannerElementId++}" }

    WebElementView(
        factory = {
            (document.createElement("div") as HTMLDivElement).apply {
                id = elementId
                style.width = "100%"
                style.height = "100%"
                style.overflowX = "hidden"
                style.overflowY = "hidden"
            }
        },
        modifier = Modifier.fillMaxSize(),
        update = { element ->
            element.style.backgroundColor = backgroundColor.toCssColor()
            if (contentDescription != null) element.setAttribute("aria-label", contentDescription)
            else element.removeAttribute("aria-label")
        },
    )

    DisposableEffect(elementId) {
        var handleNext = true
        val scanner = startQRCodeScanner(
            elementId = elementId,
            onStarted = { started ->
                currentOnIsLoadingChange(false)
                if (!started) currentOnResult(QRResult.QRError)
            },
            onDecoded = onDecoded@{ text ->
                if (!handleNext) return@onDecoded false
                val result = QRResult.QRSuccess(listOf(text)) { it } ?: return@onDecoded true
                handleNext = currentOnResult(result)
                handleNext
            },
        )
        onDispose { stopQRCodeScanner(scanner) }
    }
}

private var nextScannerElementId: Int = 0

private fun Color.toCssColor(): String =
    "rgba(${(red * 255).roundToInt()}, ${(green * 255).roundToInt()}, ${(blue * 255).roundToInt()}, $alpha)"

private suspend fun awaitVideoInputPresence(): Boolean = suspendCancellableCoroutine { continuation ->
    detectVideoInput { continuation.resume(it) }
}

private suspend fun awaitHtml5QrcodeLibrary(): Boolean = suspendCancellableCoroutine { continuation ->
    loadHtml5QrcodeLibrary(HTML5_QRCODE_URL) { continuation.resume(it) }
}

private const val HTML5_QRCODE_URL: String = "https://unpkg.com/html5-qrcode@2.3.8/html5-qrcode.min.js"

private fun detectVideoInput(onResult: (Boolean) -> Unit): Unit = js(
    """{
    const mediaDevices = window.navigator.mediaDevices;
    if (!mediaDevices || !mediaDevices.enumerateDevices || !mediaDevices.getUserMedia) {
        onResult(false);
        return;
    }
    mediaDevices.enumerateDevices()
        .then((devices) => onResult(devices.some((device) => device.kind === 'videoinput')))
        .catch(() => onResult(false));
}"""
)

/**
 * Injects the html5-qrcode script into the document (only once, even for multiple scanners)
 * and reports if the library became available.
 */
private fun loadHtml5QrcodeLibrary(url: String, onLoaded: (Boolean) -> Unit): Unit = js(
    """{
    if (window.__Html5QrcodeLibrary__) {
        onLoaded(true);
        return;
    }
    let loading = window.__inProcykComposeHtml5QrcodeLoading__;
    if (!loading) {
        loading = new Promise((resolve, reject) => {
            const script = document.createElement('script');
            script.src = url;
            script.async = true;
            script.onload = () => resolve();
            script.onerror = () => {
                script.remove();
                window.__inProcykComposeHtml5QrcodeLoading__ = undefined;
                reject();
            };
            document.head.appendChild(script);
        });
        window.__inProcykComposeHtml5QrcodeLoading__ = loading;
    }
    loading.then(() => onLoaded(!!window.__Html5QrcodeLibrary__), () => onLoaded(false));
}"""
)

/**
 * Starts scanning with the camera facing the environment (if available) and renders its preview in the element
 * with the given [elementId], as soon as it gets attached to the document.
 * Scanning is paused (with the preview still running) when [onDecoded] returns `false`.
 */
private fun startQRCodeScanner(
    elementId: String,
    onStarted: (Boolean) -> Unit,
    onDecoded: (String) -> Boolean,
): JsAny = js(
    """{
    const controller = { disposed: false, scanner: null };
    const start = () => {
        if (controller.disposed) return;
        const element = document.getElementById(elementId);
        if (!element) {
            window.requestAnimationFrame(start);
            return;
        }
        const library = window.__Html5QrcodeLibrary__;
        let scanner;
        try {
            scanner = new library.Html5Qrcode(elementId, {
                verbose: false,
                formatsToSupport: [library.Html5QrcodeSupportedFormats.QR_CODE],
            });
        } catch (e) {
            onStarted(false);
            return;
        }
        controller.scanner = scanner;
        scanner.start(
            { facingMode: 'environment' },
            { fps: 20 },
            (decodedText) => {
                if (controller.disposed || onDecoded(decodedText)) return;
                try { scanner.pause(false); } catch (e) {}
            },
            () => {}
        ).then(() => {
            if (controller.disposed) {
                scanner.stop().then(() => scanner.clear()).catch(() => {});
                return;
            }
            element.querySelectorAll('video').forEach((video) => {
                video.style.width = '100%';
                video.style.height = '100%';
                video.style.objectFit = 'cover';
            });
            onStarted(true);
        }).catch(() => {
            if (!controller.disposed) onStarted(false);
        });
    };
    start();
    return controller;
}"""
)

private fun stopQRCodeScanner(controller: JsAny): Unit = js(
    """{
    controller.disposed = true;
    const scanner = controller.scanner;
    if (!scanner) return;
    try {
        const state = scanner.getState();
        // Html5QrcodeScannerState.SCANNING = 2, Html5QrcodeScannerState.PAUSED = 3
        if (state === 2 || state === 3) scanner.stop().then(() => scanner.clear()).catch(() => {});
    } catch (e) {}
}"""
)
