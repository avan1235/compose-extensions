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
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.Path


@Immutable
internal class SquareShape(
    val size: Float = 1f
) : QrShapeModifier {

    override fun Path.path(size: Float, neighbors: Neighbors): Path = apply {
        val s = size * this@SquareShape.size.coerceIn(0f, 1f)
        val offset = (size - s) / 2

        addRect(
            Rect(
                Offset(offset, offset),
                Size(s, s)
            )
        )
    }
}

@Immutable
internal class CircleShape(
   val size: Float
) : QrShapeModifier {

    override fun Path.path(size: Float, neighbors: Neighbors): Path = apply {
        val s = size * this@CircleShape.size.coerceIn(0f, 1f)
        val offset = (size - s) / 2
        
        addOval(
            Rect(
                Offset(offset, offset),
                Size(s, s)
            )
        )
    }
}

@Immutable
internal class RoundCornersShape(
    val cornerRadius : Float,
    val withNeighbors : Boolean,
    val topLeft: Boolean = true,
    val bottomLeft: Boolean = true,
    val topRight: Boolean = true,
    val bottomRight: Boolean = true,
)  : QrShapeModifier {


    override fun Path.path(size: Float, neighbors: Neighbors): Path = apply {

        val corner = (cornerRadius.coerceIn(0f, .5f) * size).let { CornerRadius(it, it) }

        addRoundRect(
            RoundRect(
                Rect(0f, 0f, size, size),
                topLeft = if (topLeft && (withNeighbors.not() || neighbors.top.not() && neighbors.left.not()))
                    corner else CornerRadius.Zero,
                topRight = if (topRight && (withNeighbors.not() || neighbors.top.not() && neighbors.right.not()))
                    corner else CornerRadius.Zero,
                bottomRight = if (bottomRight && (withNeighbors.not() || neighbors.bottom.not() && neighbors.right.not()))
                    corner else CornerRadius.Zero,
                bottomLeft = if (bottomLeft && (withNeighbors.not() || neighbors.bottom.not() && neighbors.left.not()))
                    corner else CornerRadius.Zero
            )
        )
    }

}

@Immutable
internal class VerticalLinesShape(
    private val width : Float
) : QrShapeModifier {

    override fun Path.path(size: Float, neighbors: Neighbors): Path = apply {

        val padding = (size * (1 - width.coerceIn(0f, 1f)))

        if (neighbors.top) {
            addRect(Rect(Offset(padding, 0f), Size(size - padding * 2, size / 2)))
        } else {
            addArc(Rect(Offset(padding, 0f), Size(size - padding * 2, size)), 180f, 180f)
        }

        if (neighbors.bottom) {
            addRect(Rect(Offset(padding, size / 2), Size(size - padding * 2, size / 2)))
        } else {
            addArc(Rect(Offset(padding, 0f), Size(size - padding * 2, size)), 0f, 180f)
        }
    }
}

@Immutable
internal class HorizontalLinesShape(
    private val width : Float
) : QrShapeModifier {

    override fun Path.path(size: Float, neighbors: Neighbors): Path = apply {

        val padding = (size * (1 - width.coerceIn(0f, 1f)))

        if (neighbors.left) {
            addRect(Rect(Offset(0f, padding), Size(size / 2, size - padding * 2)))
        } else {
            addArc(Rect(Offset(0f, padding), Size(size, size - padding * 2)), 90f, 180f)

        }

        if (neighbors.right) {
            addRect(Rect(Offset(size / 2, padding), Size(size / 2, size - padding * 2)))
        } else {
            addArc(Rect(Offset(0f, padding), Size(size, size - padding * 2)), -90f, 180f)
        }
    }
}
