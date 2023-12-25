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
import `in`.procyk.compose.qrcode.options.dsl.InternalQrOptionsBuilderScope
import `in`.procyk.compose.qrcode.options.dsl.QrOptionsBuilderScope

fun QrOptions(block : QrOptionsBuilderScope.() -> Unit) : QrOptions {
    val builder = QrOptions.Builder()
    InternalQrOptionsBuilderScope(builder).apply(block)
    return builder.build()
}

/**
 * Styling options of the QR code
 *
 * @param shapes shapes of the QR code pattern and its parts
 * @param colors colors of the QR code parts
 * @param logo middle image
 * @param errorCorrectionLevel level of error correction
 * @param fourEyed enable fourth eye
 * */
@Immutable
class QrOptions(
    val shapes: QrShapes = QrShapes(),
    val colors : QrColors = QrColors(),
    val logo : QrLogo = QrLogo(),
    val errorCorrectionLevel: QrErrorCorrectionLevel = QrErrorCorrectionLevel.Auto,
    val fourEyed : Boolean = false,
) {

    fun copy(
        shapes: QrShapes = this.shapes,
        colors : QrColors = this.colors,
        logo : QrLogo = this.logo,
        errorCorrectionLevel: QrErrorCorrectionLevel = this.errorCorrectionLevel,
        fourthEyeEnabled : Boolean = this.fourEyed,
    ) = QrOptions(
        shapes = shapes,
        colors = colors,
        logo = logo,
        errorCorrectionLevel = errorCorrectionLevel,
        fourEyed = fourthEyeEnabled
    )


    override fun equals(other: Any?): Boolean {
        if (other !is QrOptions)
            return false

        return shapes == other.shapes &&
                colors == other.colors &&
                logo == other.logo &&
                errorCorrectionLevel == other.errorCorrectionLevel &&
                fourEyed == other.fourEyed
    }

    override fun hashCode(): Int {
        return (((((shapes.hashCode()) * 31) +
                colors.hashCode()) * 31 +
                logo.hashCode()) * 31 +
                errorCorrectionLevel.hashCode()) * 31 +
                fourEyed.hashCode()
    }

    internal class Builder {

        var shapes: QrShapes = QrShapes()
        var colors: QrColors = QrColors()
        var logo: QrLogo = QrLogo()
        var errorCorrectionLevel: QrErrorCorrectionLevel = QrErrorCorrectionLevel.Auto
        var fourthEyeEnabled: Boolean = false

        fun build(): QrOptions = QrOptions(
            shapes = shapes,
            colors = colors,
            logo = logo,
            errorCorrectionLevel = errorCorrectionLevel,
            fourEyed = fourthEyeEnabled
        )
    }
}

