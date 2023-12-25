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
 * Rewritten in Kotlin from the [original (GitHub)](https://github.com/kazuhikoarase/qrcode-generator/blob/master/java/src/main/java/com/d_project/qrcode/BitBuffer.java)
 *
 * @author Rafael Lins - g0dkar
 * @author Kazuhiko Arase - kazuhikoarase
 */
internal class BitBuffer {
    var buffer: IntArray
        private set
    var lengthInBits: Int
        private set
    private val increments = 32

    private operator fun get(index: Int): Boolean =
        buffer[index / 8] ushr 7 - index % 8 and 1 == 1

    fun put(num: Int, length: Int) {
        for (i in 0..<length) {
            put(num ushr length - i - 1 and 1 == 1)
        }
    }

    fun put(bit: Boolean) {
        if (lengthInBits == buffer.size * 8) {
            buffer = buffer.copyOf(buffer.size + increments)
        }
        if (bit) {
            buffer[lengthInBits / 8] = buffer[lengthInBits / 8] or (0x80 ushr lengthInBits % 8)
        }
        lengthInBits++
    }

    init {
        buffer = IntArray(increments)
        lengthInBits = 0
    }

    override fun toString(): String {
        val buffer = StringBuilder()
        for (i in 0..<lengthInBits) {
            buffer.append(if (get(i)) '1' else '0')
        }
        return buffer.toString()
    }
}
