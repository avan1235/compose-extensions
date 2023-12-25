/**
 * Based on QRose by Alexander Zhirkevich from [Github](https://github.com/alexzhirkevich/qrose)
 *
 * MIT License
 *
 * Copyright (c) 2023 Alexander Zhirkevich
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package `in`.procyk.compose.qrcode.options

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Path

/**
 * Style of the qr-code eye frame.
 */
interface QrFrameShape : QrShapeModifier {

    companion object {
        val Default : QrFrameShape = square()
    }
}

fun QrFrameShape.Companion.square(size: Float = 1f) : QrFrameShape =
    SquareFrameShape(size)

fun QrFrameShape.Companion.circle(size: Float = 1f) : QrFrameShape =
    CircleFrameShape(size)

fun QrFrameShape.Companion.roundCorners(
    corner: Float,
    width: Float = 1f,
    topLeft: Boolean = true,
    bottomLeft: Boolean = true,
    topRight: Boolean = true,
    bottomRight: Boolean = true,
) : QrFrameShape = RoundCornersFrameShape(
    corner = corner,
    width = width,
    topLeft = topLeft,
    bottomLeft = bottomLeft,
    topRight = topRight,
    bottomRight = bottomRight
)

fun QrFrameShape.Companion.asPixel(pixelShape: QrPixelShape) : QrFrameShape =
    AsPixelFrameShape(pixelShape)


@Immutable
private class AsPixelFrameShape(
    val pixelShape: QrPixelShape
) : QrFrameShape {

    override fun Path.path(size: Float, neighbors: Neighbors): Path = apply {

        val matrix = QrCodeMatrix(7)

        repeat(7) { i ->
            repeat(7) { j ->
                matrix[i, j] = if (i == 0 || j == 0 || i == 6 || j == 6)
                    QrCodeMatrix.PixelType.DarkPixel else QrCodeMatrix.PixelType.Background
            }
        }

        repeat(7) { i ->
            repeat(7) { j ->
                if (matrix[i, j] == QrCodeMatrix.PixelType.DarkPixel)
                    addPath(
                        pixelShape.newPath(
                            size / 7,
                            matrix.neighbors(i, j)
                        ),
                        Offset(size / 7 * i, size / 7 * j)
                    )
            }
        }
    }
}

@Immutable
private class SquareFrameShape(
    private val size: Float = 1f
) : QrFrameShape {
    override fun Path.path(size: Float, neighbors: Neighbors): Path = apply {
        val width = size / 7f * this@SquareFrameShape.size.coerceAtLeast(0f)

        addRect(
            Rect(0f, 0f, size, size)
        )
        addRect(
            Rect(width, width, size - width, size - width)
        )
    }
}


@Immutable
private class CircleFrameShape(
    private val size: Float = 1f
) : QrFrameShape {
    override fun Path.path(size: Float, neighbors: Neighbors): Path = apply {
        val width = size / 7f * this@CircleFrameShape.size.coerceAtLeast(0f)

        addOval(
            Rect(0f, 0f, size, size)
        )
        addOval(
            Rect(width, width, size - width, size - width)
        )
    }
}

@Immutable
private class RoundCornersFrameShape(
    private val corner: Float,
    private val width: Float = 1f,
    private val topLeft: Boolean = true,
    private val bottomLeft: Boolean = true,
    private val topRight: Boolean = true,
    private val bottomRight: Boolean = true,
) : QrFrameShape {
    override fun Path.path(size: Float, neighbors: Neighbors): Path = apply{

        val width = size / 7f * width.coerceAtLeast(0f)

        val realCorner = corner.coerceIn(0f, .5f)
        val outerCornerSize = realCorner * size
        val innerCornerSize = realCorner * (size - 4 * width)

        val outer = CornerRadius(outerCornerSize, outerCornerSize)
        val inner = CornerRadius(innerCornerSize, innerCornerSize)

        addRoundRect(
            RoundRect(
                Rect(0f, 0f, size, size),
                topLeft = if (topLeft) outer else CornerRadius.Zero,
                topRight = if (topRight) outer else CornerRadius.Zero,
                bottomLeft = if (bottomLeft) outer else CornerRadius.Zero,
                bottomRight = if (bottomRight) outer else CornerRadius.Zero,
            )
        )
        addRoundRect(
            RoundRect(
                Rect(width, width, size - width, size - width),
                topLeft = if (topLeft) inner else CornerRadius.Zero,
                topRight = if (topRight) inner else CornerRadius.Zero,
                bottomLeft = if (bottomLeft) inner else CornerRadius.Zero,
                bottomRight = if (bottomRight) inner else CornerRadius.Zero,
            )
        )
    }
}
