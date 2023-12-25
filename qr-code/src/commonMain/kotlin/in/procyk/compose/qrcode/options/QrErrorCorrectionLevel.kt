@file:Suppress("UNUSED")

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
import `in`.procyk.compose.qrcode.ErrorCorrectionLevel


/**
 * QR code allows you to read encoded information even if a
 * part of the QR code image is damaged. It also allows to have logo
 * inside the code as a part of "damage".
 * */
@Immutable
enum class QrErrorCorrectionLevel(
    internal val lvl : ErrorCorrectionLevel
) {

    /**
     * Minimum possible level will be used.
     * */
    Auto(ErrorCorrectionLevel.L),

    /**
     * ~7% of QR code can be damaged (or used as logo).
     * */
    Low(ErrorCorrectionLevel.L),

    /**
     * ~15% of QR code can be damaged (or used as logo).
     * */
    Medium(ErrorCorrectionLevel.M),

    /**
     * ~25% of QR code can be damaged (or used as logo).
     * */
    MediumHigh(ErrorCorrectionLevel.Q),

    /**
     * ~30% of QR code can be damaged (or used as logo).
     * */
    High(ErrorCorrectionLevel.H)
}
