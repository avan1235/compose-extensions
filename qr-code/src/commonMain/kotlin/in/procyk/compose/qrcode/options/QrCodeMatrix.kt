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

class QrCodeMatrix(val size : Int, initialFill : PixelType = PixelType.Background){

    constructor(list : List<List<PixelType>>) : this(list.size) {
        types = list.flatten().toMutableList()
    }

    enum class PixelType { DarkPixel, LightPixel, Background, Logo }

    private var types = MutableList(size * size) {
        initialFill
    }

    operator fun get(i : Int, j : Int) : PixelType {

        val outOfBound = when {
            i !in 0 until size -> i
            j !in 0 until size -> j
            else -> null
        }

        if (outOfBound != null)
            throw IndexOutOfBoundsException(
                "Index $outOfBound is out of 0..${size -1} matrix bound"
            )

        return types[i + j * size]
    }

    operator fun set(i: Int, j: Int, type: PixelType) {

        val outOfBound = when {
            i !in 0 until size -> i
            j !in 0 until size -> j
            else -> null
        }

        if (outOfBound != null)
            throw IndexOutOfBoundsException(
                "Index $outOfBound is out of 0..${size - 1} matrix bound"
            )

        types[i + j * size] = type
    }

    fun copy() : QrCodeMatrix = QrCodeMatrix(size).apply {
        types = ArrayList(this@QrCodeMatrix.types)
    }
}

internal fun QrCodeMatrix.neighbors(i : Int, j : Int) : Neighbors {

    fun cmp(i2 : Int, j2 : Int) = kotlin.runCatching {
        this[i2,j2] == this[i,j]
    }.getOrDefault(false)

    return Neighbors(
        topLeft = cmp(i - 1, j - 1),
        topRight = cmp(i + 1, j - 1),
        left = cmp(i-1, j),
        top = cmp(i, j-1),
        right = cmp(i+1, j),
        bottomLeft = cmp(i-1, j + 1),
        bottom = cmp(i, j+1),
        bottomRight = cmp(i+1, j + 1)
    )
}

