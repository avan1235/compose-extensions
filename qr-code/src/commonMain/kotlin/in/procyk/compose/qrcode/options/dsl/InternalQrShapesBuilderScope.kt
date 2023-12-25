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

import `in`.procyk.compose.qrcode.options.*


internal class InternalQrShapesBuilderScope(
    private val builder: QrOptions.Builder,
    centralSymmetry : Boolean,
) : QrShapesBuilderScope {

    init {
        builder.shapes = builder.shapes.copy(
            centralSymmetry = centralSymmetry
        )
    }

    override var pattern: QrCodeShape
        get() = builder.shapes.code
        set(value) = with(builder){
            shapes = shapes.copy(
                code = value
            )
        }


    override var darkPixel: QrPixelShape
        get() = builder.shapes.darkPixel
        set(value) = with(builder){
            shapes = shapes.copy(
                darkPixel = value
            )
        }

    override var lightPixel: QrPixelShape
        get() = builder.shapes.lightPixel
        set(value) = with(builder){
            shapes = shapes.copy(
                lightPixel = value
            )
        }

    override var ball: QrBallShape
        get() = builder.shapes.ball
        set(value) = with(builder){
            shapes = shapes.copy(
                ball = value
            )
        }

    override var frame: QrFrameShape
        get() = builder.shapes.frame
        set(value) = with(builder){
            shapes = shapes.copy(
                frame = value
            )
        }
}