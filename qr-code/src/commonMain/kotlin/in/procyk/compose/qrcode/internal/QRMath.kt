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

/**
 * Rewritten in Kotlin from the [original (GitHub)](https://github.com/kazuhikoarase/qrcode-generator/blob/master/java/src/main/java/com/d_project/qrcode/QRMath.java)
 *
 * @author Rafael Lins - g0dkar
 * @author Kazuhiko Arase - kazuhikoarase
 */
internal object QRMath {
    private val EXP_TABLE = IntArray(256)
    private val LOG_TABLE = IntArray(256)

    fun glog(n: Int): Int = LOG_TABLE[n]

    fun gexp(n: Int): Int {
        var i = n
        while (i < 0) {
            i += 255
        }
        while (i >= 256) {
            i -= 255
        }
        return EXP_TABLE[i]
    }

    init {
        for (i in 0..7) {
            EXP_TABLE[i] = 1 shl i
        }

        for (i in 8..255) {
            EXP_TABLE[i] = (
                EXP_TABLE[i - 4]
                    xor EXP_TABLE[i - 5]
                    xor EXP_TABLE[i - 6]
                    xor EXP_TABLE[i - 8]
                )
        }

        for (i in 0..254) {
            LOG_TABLE[EXP_TABLE[i]] = i
        }
    }
}
