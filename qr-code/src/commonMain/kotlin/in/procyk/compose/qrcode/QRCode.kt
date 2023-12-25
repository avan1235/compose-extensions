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

import `in`.procyk.compose.qrcode.QRCodeDataType.*
import `in`.procyk.compose.qrcode.internal.*
import `in`.procyk.compose.qrcode.internal.QRCodeSetup.applyMaskPattern
import `in`.procyk.compose.qrcode.internal.QRCodeSetup.setupBottomLeftPositionProbePattern
import `in`.procyk.compose.qrcode.internal.QRCodeSetup.setupPositionAdjustPattern
import `in`.procyk.compose.qrcode.internal.QRCodeSetup.setupTimingPattern
import `in`.procyk.compose.qrcode.internal.QRCodeSetup.setupTopLeftPositionProbePattern
import `in`.procyk.compose.qrcode.internal.QRCodeSetup.setupTopRightPositionProbePattern
import `in`.procyk.compose.qrcode.internal.QRCodeSetup.setupTypeInfo
import `in`.procyk.compose.qrcode.internal.QRCodeSetup.setupTypeNumber
import `in`.procyk.compose.qrcode.options.QrCodeMatrix
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

internal class QRCode @JvmOverloads constructor(
    private val data: String,
    private val errorCorrectionLevel: ErrorCorrectionLevel = ErrorCorrectionLevel.M,
    private val dataType: QRCodeDataType = QRUtil.getDataType(data)
) {
    private val qrCodeData: QRData = when (dataType) {
        NUMBERS -> QRNumber(data)
        UPPER_ALPHA_NUM -> QRAlphaNum(data)
        DEFAULT -> QR8BitByte(data)
    }

    companion object {
        const val DEFAULT_CELL_SIZE = 1
        private const val PAD0 = 0xEC
        private const val PAD1 = 0x11

        /**
         * Calculates a suitable value for the [dataType] field for you.
         */
        @JvmStatic
        @JvmOverloads
        fun typeForDataAndECL(
            data: String,
            errorCorrectionLevel: ErrorCorrectionLevel,
            dataType: QRCodeDataType = QRUtil.getDataType(data)
        ): Int {
            val qrCodeData = when (dataType) {
                NUMBERS -> QRNumber(data)
                UPPER_ALPHA_NUM -> QRAlphaNum(data)
                DEFAULT -> QR8BitByte(data)
            }
            val dataLength = qrCodeData.length()

            for (typeNum in 1 until errorCorrectionLevel.maxTypeNum) {
                if (dataLength <= QRUtil.getMaxLength(typeNum, dataType, errorCorrectionLevel)) {
                    return typeNum
                }
            }

            return 40
        }
    }



    @JvmOverloads
    fun encode(
        type: Int = typeForDataAndECL(data, errorCorrectionLevel),
        maskPattern: MaskPattern = MaskPattern.PATTERN000
    ): QrCodeMatrix {
        val moduleCount = type * 4 + 17
        val modules: Array<Array<QRCodeSquare?>> =
            Array(moduleCount) { Array(moduleCount) { null } }

        setupTopLeftPositionProbePattern(modules)
        setupTopRightPositionProbePattern(modules)
        setupBottomLeftPositionProbePattern(modules)

        setupPositionAdjustPattern(type, modules)
        setupTimingPattern(moduleCount, modules)
        setupTypeInfo(errorCorrectionLevel, maskPattern, moduleCount, modules)

        if (type >= 7) {
            setupTypeNumber(type, moduleCount, modules)
        }

        val data = createData(type)

        applyMaskPattern(data, maskPattern, moduleCount, modules)

        return QrCodeMatrix(
            modules.map {
                it.map { pixel ->
                    if (pixel?.dark == true)
                        QrCodeMatrix.PixelType.DarkPixel
                    else QrCodeMatrix.PixelType.LightPixel
                }
            }
        )
    }

    private fun createData(type: Int): IntArray {
        val rsBlocks = RSBlock.getRSBlocks(type, errorCorrectionLevel)
        val buffer = BitBuffer()

        buffer.put(qrCodeData.dataType.value, 4)
        buffer.put(qrCodeData.length(), qrCodeData.getLengthInBits(type))
        qrCodeData.write(buffer)

        val totalDataCount = rsBlocks.sumOf { it.dataCount } * 8

        if (buffer.lengthInBits > totalDataCount) {
            throw IllegalArgumentException("Code length overflow (${buffer.lengthInBits} > $totalDataCount)")
        }

        if (buffer.lengthInBits + 4 <= totalDataCount) {
            buffer.put(0, 4)
        }

        while (buffer.lengthInBits % 8 != 0) {
            buffer.put(false)
        }

        while (true) {
            if (buffer.lengthInBits >= totalDataCount) {
                break
            }

            buffer.put(PAD0, 8)

            if (buffer.lengthInBits >= totalDataCount) {
                break
            }

            buffer.put(PAD1, 8)
        }

        return createBytes(buffer, rsBlocks)
    }

    private fun createBytes(buffer: BitBuffer, rsBlocks: Array<RSBlock>): IntArray {
        var offset = 0
        var maxDcCount = 0
        var maxEcCount = 0
        var totalCodeCount = 0
        val dcData = Array(rsBlocks.size) { IntArray(0) }
        val ecData = Array(rsBlocks.size) { IntArray(0) }

        rsBlocks.forEachIndexed { i, it ->
            val dcCount = it.dataCount
            val ecCount = it.totalCount - dcCount

            totalCodeCount += it.totalCount
            maxDcCount = maxDcCount.coerceAtLeast(dcCount)
            maxEcCount = maxEcCount.coerceAtLeast(ecCount)

            // Init dcData[i]
            dcData[i] = IntArray(dcCount) { idx -> 0xff and buffer.buffer[idx + offset] }
            offset += dcCount

            // Init ecData[i]
            val rsPoly = QRUtil.getErrorCorrectPolynomial(ecCount)
            val rawPoly = Polynomial(dcData[i], rsPoly.len() - 1)
            val modPoly = rawPoly.mod(rsPoly)
            val ecDataSize = rsPoly.len() - 1

            ecData[i] = IntArray(ecDataSize) { idx ->
                val modIndex = idx + modPoly.len() - ecDataSize
                if ((modIndex >= 0)) modPoly[modIndex] else 0
            }
        }

        var index = 0
        val data = IntArray(totalCodeCount)

        for (i in 0 until maxDcCount) {
            for (r in rsBlocks.indices) {
                if (i < dcData[r].size) {
                    data[index++] = dcData[r][i]
                }
            }
        }

        for (i in 0 until maxEcCount) {
            for (r in rsBlocks.indices) {
                if (i < ecData[r].size) {
                    data[index++] = ecData[r][i]
                }
            }
        }

        return data
    }

    override fun toString(): String =
        "QRCode(data=$data" +
            ", errorCorrectionLevel=$errorCorrectionLevel" +
            ", dataType=$dataType" +
            ", qrCodeData=${qrCodeData::class.simpleName}" +
            ")"
}

