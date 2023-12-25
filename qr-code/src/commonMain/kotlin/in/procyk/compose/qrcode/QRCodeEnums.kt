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


/**
 * The level of Error Correction to apply to the QR Code image. The Higher the Error Correction, the lower quality
 * **print** the QRCode can be (think of "wow, even with the paper a bit crumpled, it still read the QR Code!" - that
 * is likely a [Q] or [H] error correction).
 *
 * The trade-off is the amount of data you can encode. The higher the error correction level, the less amount of data
 * you'll be able to encode.
 *
 * Please consult [Kazuhiko's Online Demo](https://kazuhikoarase.github.io/qrcode-generator/js/demo/) where at the time
 * of writing a handy table showed how many bytes can be encoded given a data type ([QRCodeDataType]) and Error Correction Level.
 *
 * This library automatically tries to fit ~2048 bytes into the QR Code regardless of error correction level. That is
 * the reason and meaning of [maxTypeNum].
 *
 * Rewritten in Kotlin from the [original (GitHub)](https://github.com/kazuhikoarase/qrcode-generator/blob/master/java/src/main/java/com/d_project/qrcode/ErrorCorrectionLevel.java)
 *
 * @param value Value associated with this error correction level
 * @param maxTypeNum Maximum `type` value which can fit 2048 bytes. Used to automatically calculate the `type` value.
 *
 * @author Rafael Lins - g0dkar
 * @author Kazuhiko Arase - kazuhikoarase
 */
internal enum class ErrorCorrectionLevel(val value: Int, val maxTypeNum: Int) {
    L(1, 21),
    M(0, 25),
    Q(3, 30),
    H(2, 34)
}

/**
 * Patterns to apply to the QRCode. They change how the QRCode looks in the end.
 *
 * Rewritten in Kotlin from the [original (GitHub)](https://github.com/kazuhikoarase/qrcode-generator/blob/master/java/src/main/java/com/d_project/qrcode/MaskPattern.java)
 *
 * @author Rafael Lins - g0dkar
 * @author Kazuhiko Arase - kazuhikoarase
 */
internal enum class MaskPattern {
    /** This is the default pattern (no pattern is applied) */
    PATTERN000,
    PATTERN001,
    PATTERN010,
    PATTERN011,
    PATTERN100,
    PATTERN101,
    PATTERN110,
    PATTERN111
}

/**
 * QRCode Modes. Basically represents which kind of data is being encoded.
 *
 * Rewritten in Kotlin from the [original (GitHub)](https://github.com/kazuhikoarase/qrcode-generator/blob/master/java/src/main/java/com/d_project/qrcode/Mode.java)
 *
 * @author Rafael Lins - g0dkar
 * @author Kazuhiko Arase - kazuhikoarase
 */
internal enum class QRCodeDataType(val value: Int) {
    /** Strictly numerical data. Like huge integers. These can be way bigger than [Long.MAX_VALUE]. */
    NUMBERS(1 shl 0),

    /** Represents Uppercase Alphanumerical data. Allowed characters: `[0-9A-Z $%*+\-./:]`. */
    UPPER_ALPHA_NUM(1 shl 1),

    /** This can be any kind of data. With this you can encode Strings, URLs, images, files, anything. */
    DEFAULT(1 shl 2)
}
