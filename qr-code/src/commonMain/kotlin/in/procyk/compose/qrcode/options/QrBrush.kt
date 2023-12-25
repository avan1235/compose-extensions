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
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.painter.Painter
import `in`.procyk.compose.qrcode.options.QrBrushMode.Separate
import `in`.procyk.compose.qrcode.toImageBitmap
import kotlin.random.Random

enum class QrBrushMode {

    /**
     * If applied to QR code pattern, the whole pattern will be combined to the single [Path]
     * and then painted using produced [Brush] with large size and [Neighbors.Empty].
     *
     * Balls and frames with [QrBrush.Unspecified] will also be joined with this path.
     * If balls or frames have specified [QrBrush] then [Neighbors] parameter will be passed
     * according to eye position
     * */
    Join,

    /**
     * If applied to QR code pattern, each QR code pixel will be painted separately and for each
     * pixel new [Brush] will be created. In this scenario [Neighbors] parameter for pixels will
     * be chosen according to the actual pixel neighbors.
     *
     * Balls and frames with [QrBrush.Unspecified] will be painted with [QrBrush.Default].
     * */
    Separate
}

/**
 * Color [Brush] factory for a QR code part.
 * */
interface QrBrush  {

    /**
     * Brush [mode] indicates the way this brush is applied to the QR code part.
     * */
    val mode: QrBrushMode

    /**
     * Factory method of the [Brush] for the element with given [size] and [neighbors].
     * */
    fun brush(size: Float, neighbors: Neighbors): Brush

    companion object {

        /**
         * Delegates painting to other most suitable brush
         * */
        val Unspecified : QrBrush = solid(Color.Unspecified)

        /**
         * Default solid black brush
         * */
        val Default : QrBrush = solid(Color.Black)
    }
}


/**
 * Check if this brush is not specified
 * */
val QrBrush.isUnspecified
    get() = this === QrBrush.Unspecified || this is Solid && this.color.isUnspecified

/**
 * Check if this brush is specified
 * */
val QrBrush.isSpecified : Boolean
    get() = !isUnspecified

/**
 * [SolidColor] brush from [color]
 * */
fun QrBrush.Companion.solid(color: Color) : QrBrush = Solid(color)

/**
 * Any Compose brush constructed in [builder] with specific QR code part size.
 * This can be gradient brushes like, shader brush or any other.
 *
 * Example:
 * ```
 * QrBrush.brush {
 *     Brush.linearGradient(
 *         0f to Color.Red,
 *         1f to Color.Blue,
 *         end = Offset(it,it)
 *     )
 * }
 * ```
 * */
fun QrBrush.Companion.brush(
    builder: (size : Float) -> Brush
) : QrBrush = BrushColor(builder)

/**
 * Random solid color picked from given [probabilities].
 * Custom source of [random]ness can be used.
 *
 * Note: This brush uses [Separate] brush mode.
 *
 * Example:
 * ```
 *  QrBrush.random(
 *      0.05f to Color.Red,
 *      1f to Color.Black
 *  )
 * ```
 * */
fun QrBrush.Companion.random(
    vararg probabilities: Pair<Float, Color>,
    random: Random = Random(13)
) : QrBrush = Random(probabilities.toList(), random)

/**
 * Shader brush that resizes the image [painter] to the required size.
 * [painter] resolution should be square for better result.
 * */
fun QrBrush.Companion.image(
    painter: Painter,
    alpha : Float = 1f,
    colorFilter: ColorFilter? = null
) : QrBrush = Image(painter, alpha, colorFilter)



@Immutable
private class Image(
    private val painter: Painter,
    private val alpha : Float = 1f,
    private val colorFilter: ColorFilter? = null
) : QrBrush {

    override val mode: QrBrushMode
        get() = QrBrushMode.Join

    private var cachedBrush: Brush? = null
    private var cachedSize: Int = -1

    override fun brush(size: Float, neighbors: Neighbors): Brush {

        val intSize = size.toInt()

        if (cachedBrush != null && cachedSize == intSize) {
            return cachedBrush!!
        }

        val bmp = painter.toImageBitmap(intSize, intSize, alpha, colorFilter)

        cachedBrush = ShaderBrush(ImageShader(bmp, TileMode.Decal, TileMode.Decal))
        cachedSize = intSize

        return cachedBrush!!
    }
}

@Immutable
private class Solid(val color: Color) : QrBrush by BrushColor({ SolidColor(color) })

@Immutable
private class BrushColor(private val builder: (size : Float) -> Brush) : QrBrush {
    override val mode: QrBrushMode
        get() = QrBrushMode.Join

    override fun brush(size: Float, neighbors: Neighbors): Brush = this.builder(size)
}

@Immutable
private class Random(
    private val probabilities: List<Pair<Float, Color>>,
    private val random : Random
) : QrBrush {

    private val _probabilities = mutableListOf<Pair<ClosedFloatingPointRange<Float>,Color>>()

    init {
        require(probabilities.isNotEmpty()) {
            "Random color list can't be empty"
        }
        (listOf(0f) + probabilities.map { it.first }).reduceIndexed { index, sum, i ->
            _probabilities.add(sum..(sum + i) to probabilities[index - 1].second)
            sum + i
        }
    }


    override val mode: QrBrushMode = QrBrushMode.Separate

    override fun brush(size: Float, neighbors: Neighbors): Brush {
        val random = random.nextFloat() * _probabilities.last().first.endInclusive

        val idx = _probabilities.binarySearch {
            when {
                random < it.first.start -> 1
                random > it.first.endInclusive -> -1
                else -> 0
            }
        }

        return SolidColor(probabilities[idx].second)
    }
}
