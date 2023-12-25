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


/**
 * Status of the neighbor QR code pixels or eyes
 * */

@Immutable
class Neighbors(
    val topLeft : Boolean = false,
    val topRight : Boolean = false,
    val left : Boolean = false,
    val top : Boolean = false,
    val right : Boolean = false,
    val bottomLeft: Boolean = false,
    val bottom: Boolean = false,
    val bottomRight: Boolean = false,
) {

    companion object {
        val Empty = Neighbors()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Neighbors

        if (topLeft != other.topLeft) return false
        if (topRight != other.topRight) return false
        if (left != other.left) return false
        if (top != other.top) return false
        if (right != other.right) return false
        if (bottomLeft != other.bottomLeft) return false
        if (bottom != other.bottom) return false
        if (bottomRight != other.bottomRight) return false

        return true
    }

    override fun hashCode(): Int {
        var result = topLeft.hashCode()
        result = 31 * result + topRight.hashCode()
        result = 31 * result + left.hashCode()
        result = 31 * result + top.hashCode()
        result = 31 * result + right.hashCode()
        result = 31 * result + bottomLeft.hashCode()
        result = 31 * result + bottom.hashCode()
        result = 31 * result + bottomRight.hashCode()
        return result
    }
}
val Neighbors.hasAny : Boolean
    get() = topLeft || topRight || left || top ||
            right || bottomLeft || bottom || bottomRight

val Neighbors.hasAllNearest
    get() = top && bottom && left && right

val Neighbors.hasAll : Boolean
    get() = topLeft && topRight && left && top &&
            right && bottomLeft && bottom && bottomRight

