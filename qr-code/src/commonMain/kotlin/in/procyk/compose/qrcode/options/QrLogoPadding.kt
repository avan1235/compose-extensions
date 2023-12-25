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
 * Type of padding applied to the logo.
 * Helps to highlight the logo inside the QR code pattern.
 * Padding can be added regardless of the presence of a logo.
 * */
sealed interface QrLogoPadding {

    /**
     * Padding size relatively to the size of logo
     * */
    val size : Float


    /**
     * Logo will be drawn on top of QR code without any padding.
     * QR code pixels might be visible through transparent logo.
     *
     * Prefer empty padding if your qr code encodes large amount of data
     * to avoid performance issues.
     * */
    @Immutable
    data object Empty : QrLogoPadding {
        override val size: Float get() = 0f
    }


    /**
     * Padding will be applied precisely according to the shape of logo
     * */
    @Immutable
    class Accurate(override val size: Float) : QrLogoPadding


    /**
     * Works like [Accurate] but all clipped pixels will be removed.
     *
     * WARNING: this padding can cause performance issues
     * for QR codes with large amount out data
     * */
    @Immutable
    class Natural(override val size: Float) : QrLogoPadding
}