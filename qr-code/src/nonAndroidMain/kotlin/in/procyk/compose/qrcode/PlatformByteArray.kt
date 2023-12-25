package `in`.procyk.compose.qrcode

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import `in`.procyk.compose.qrcode.ImageFormat.JPEG
import `in`.procyk.compose.qrcode.ImageFormat.PNG
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image

actual fun ImageBitmap.toByteArray(format: ImageFormat): ByteArray {
    val data = Image
        .makeFromBitmap(asSkiaBitmap())
        .encodeToData(format.toEncodedImageFormat())
        ?: error("This painter cannot be encoded to $format")
    return data.bytes
}

private fun ImageFormat.toEncodedImageFormat(): EncodedImageFormat = when (this) {
    PNG -> EncodedImageFormat.PNG
    JPEG -> EncodedImageFormat.JPEG
}