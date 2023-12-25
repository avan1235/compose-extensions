package `in`.procyk.compose.qrcode

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import java.io.ByteArrayOutputStream

actual fun ImageBitmap.toByteArray(format: ImageFormat): ByteArray = ByteArrayOutputStream().use {
    asAndroidBitmap().compress(format.toCompressFormat(), 100, it)
    it.toByteArray()
}

private fun ImageFormat.toCompressFormat(): Bitmap.CompressFormat = when (this) {
    ImageFormat.PNG -> Bitmap.CompressFormat.PNG
    ImageFormat.JPEG -> Bitmap.CompressFormat.JPEG
}
