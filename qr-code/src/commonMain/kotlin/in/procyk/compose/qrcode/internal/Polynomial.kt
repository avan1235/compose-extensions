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
package `in`.procyk.compose.qrcode.internal

import `in`.procyk.compose.qrcode.internal.QRMath.gexp
import `in`.procyk.compose.qrcode.internal.QRMath.glog

/**
 * Rewritten in Kotlin from the [original (GitHub)](https://github.com/kazuhikoarase/qrcode-generator/blob/master/java/src/main/java/com/d_project/qrcode/Polynomial.java)
 *
 * @author Rafael Lins - g0dkar
 * @author Kazuhiko Arase - kazuhikoarase
 */
internal class Polynomial(num: IntArray, shift: Int = 0) {
    private val data: IntArray

    init {
        val offset = num.indexOfFirst { it != 0 }.coerceAtLeast(0)
        this.data = IntArray(num.size - offset + shift) { 0 }
        arraycopy(num, offset, this.data, 0, num.size - offset)
    }

    private fun arraycopy(from: IntArray, fromPos: Int, to: IntArray, toPos: Int, length: Int) {
        for (i in 0..<length) {
            to[toPos + i] = from[fromPos + i]
        }
    }

    operator fun get(i: Int) = data[i]

    fun len(): Int = data.size

    fun multiply(other: Polynomial): Polynomial =
        IntArray(len() + other.len() - 1) { 0 }
            .let {
                for (i in 0..<len()) {
                    for (j in 0..<other.len()) {
                        it[i + j] = it[i + j] xor gexp(glog(this[i]) + glog(other[j]))
                    }
                }

                Polynomial(it)
            }

    fun mod(other: Polynomial): Polynomial =
        if (len() - other.len() < 0) {
            this
        } else {
            val ratio = glog(this[0]) - glog(other[0])
            val result = data.copyOf()

            other.data.forEachIndexed { i, it ->
                result[i] = result[i] xor gexp(glog(it) + ratio)
            }

            Polynomial(result).mod(other)
        }
}
