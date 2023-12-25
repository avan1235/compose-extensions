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
package `in`.procyk.compose.qrcode.options.dsl

import `in`.procyk.compose.qrcode.options.QrBrush
import `in`.procyk.compose.qrcode.options.QrOptions

internal class InternalQrColorsBuilderScope(
    private val builder: QrOptions.Builder
) : QrColorsBuilderScope {

    override var dark: QrBrush
        get() = builder.colors.dark
        set(value) = with(builder) {
            colors = colors.copy(
                dark = value
            )
        }

    override var light: QrBrush
        get() = builder.colors.light
        set(value) = with(builder) {
            colors = colors.copy(
                light = value
            )
        }

    override var frame: QrBrush
        get() = builder.colors.frame
        set(value) = with(builder) {
            colors = colors.copy(
                frame = value
            )
        }


    override var ball: QrBrush
        get() = builder.colors.ball
        set(value) = with(builder) {
            colors = colors.copy(
                ball = value
            )
        }
}