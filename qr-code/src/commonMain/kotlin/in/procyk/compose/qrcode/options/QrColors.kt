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
 * Colors of QR code elements
 *
 * @param dark brush of the dark QR code pixels
 * @param light brush of the light QR code pixels
 * @param ball brush of the QR code eye balls
 * @param frame brush of the QR code eye frames
 */
@Immutable
class QrColors(
    val dark : QrBrush = QrBrush.Default,
    val light : QrBrush = QrBrush.Unspecified,
    val ball : QrBrush = QrBrush.Unspecified,
    val frame : QrBrush = QrBrush.Unspecified,
) {
    fun copy(
        dark : QrBrush = this.dark,
        light : QrBrush = this.light,
        ball : QrBrush = this.ball,
        frame : QrBrush = this.frame
    ) = QrColors(dark, light, ball, frame)
}