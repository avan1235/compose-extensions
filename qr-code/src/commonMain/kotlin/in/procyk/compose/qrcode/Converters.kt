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
package `in`.procyk.compose.qrcode

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

/**
 * Converts [Painter] to image with desired [width], [height] and [format] and returns its bytes.
 * */
fun Painter.toByteArray(width : Int, height: Int, format : ImageFormat = ImageFormat.PNG) : ByteArray =
    toImageBitmap(width, height).toByteArray(format)

/**
 * Converts [Painter] to [ImageBitmap] with desired [width], [height], [alpha] and [colorFilter]
 * */
fun Painter.toImageBitmap(
    width : Int,
    height : Int,
    alpha : Float = 1f,
    colorFilter: ColorFilter? = null
) : ImageBitmap {

    val bmp = ImageBitmap(width, height)
    val canvas = Canvas(bmp)

    CanvasDrawScope().draw(
        density = Density(1f, 1f),
        layoutDirection = LayoutDirection.Ltr,
        canvas = canvas,
        size = Size(width.toFloat(), height.toFloat())
    ) {
        draw(this@draw.size, alpha, colorFilter)
    }

    return bmp
}