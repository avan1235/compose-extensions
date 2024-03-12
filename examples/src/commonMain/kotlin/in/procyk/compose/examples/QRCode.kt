package `in`.procyk.compose.examples

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import `in`.procyk.compose.qrcode.QrData
import `in`.procyk.compose.qrcode.options.*
import `in`.procyk.compose.qrcode.rememberQrCodePainter

@Composable
internal fun QRCode(
    data: String = QrData.text("https://github.com/avan1235/shin"),
    onClose: () -> Unit,
) = ExampleSystemBarsScreen(onClose) {
    Image(
        painter = rememberQrCodePainter(data) {
            shapes {
                ball = QrBallShape.circle()
                darkPixel = QrPixelShape.roundCorners()
                frame = QrFrameShape.roundCorners(.25f)
            }
            colors {
                dark = QrBrush.solid(Color.Black)
            }
        },
        contentDescription = "QR Code"
    )
}
