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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path

/**
 * Style of the qr-code eye internal ball.
 * */
interface QrBallShape : QrShapeModifier {

    companion object {
        val Default : QrBallShape = square()
    }
}

fun QrBallShape.Companion.square(size : Float = 1f) : QrBallShape =
    object : QrBallShape, QrShapeModifier by SquareShape(size) {}

fun QrBallShape.Companion.circle(size : Float = 1f) : QrBallShape =
    object : QrBallShape, QrShapeModifier by CircleShape(size) {}

fun QrBallShape.Companion.roundCorners(
    radius: Float,
    topLeft: Boolean = true,
    bottomLeft: Boolean = true,
    topRight: Boolean = true,
    bottomRight: Boolean = true,
) : QrBallShape = object : QrBallShape, QrShapeModifier by RoundCornersShape(
    cornerRadius = radius,
    topLeft = topLeft,
    bottomLeft = bottomLeft,
    topRight = topRight,
    bottomRight = bottomRight,
    withNeighbors = false
) {}

fun QrBallShape.Companion.asPixel(pixelShape: QrPixelShape) : QrBallShape =
    AsPixelBallShape(pixelShape)



@Immutable
private class AsPixelBallShape(
    private val pixelShape: QrPixelShape
) : QrBallShape {

    override fun Path.path(size: Float, neighbors: Neighbors): Path = apply {

        val matrix = QrCodeMatrix(3, QrCodeMatrix.PixelType.DarkPixel)

        repeat(3){ i ->
            repeat(3){ j ->
                addPath(
                    pixelShape.newPath(
                        size / 3,
                        matrix.neighbors(i,j)
                    ),
                    Offset(size/3 * i, size/3 * j)
                )
            }
        }
    }
}