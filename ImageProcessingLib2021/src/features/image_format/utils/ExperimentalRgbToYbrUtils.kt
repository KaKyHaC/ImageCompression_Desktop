package features.image_format.utils

import java.lang.Math.round

object ExperimentalRgbToYbrUtils {


    var ktoYCbCr = arrayOf(
            floatArrayOf(0.299f, 0.587f, 0.114f),
            floatArrayOf(-0.168736f, -0.331264f, 0.5f),
            floatArrayOf(0.5f, -0.418688f, -0.081312f)
    )

    var ktoRGB = arrayOf(
            floatArrayOf(1f, 0f, 1.402f),
            floatArrayOf(1f, -0.34414f, -0.71414f),
            floatArrayOf(1f, 1.772f, 0f)
    )

    /**
     * "Переход к YCbCr."
     */
    fun RGBtoYCC(fxs: Int, fys: Int, mas0R: Array<ShortArray>, mas0G: Array<ShortArray>, mas0B: Array<ShortArray>) {
        val mas0Y = Array(fxs) { ShortArray(fys) }
        val mas0Cr = Array(fxs) { ShortArray(fys) }
        val mas0Cb = Array(fxs) { ShortArray(fys) }
        for (x in 0 until fxs) {
            for (y in 0 until fys) {
                //Округление и обрезка диапазона*************************************************************************************************************************************
                mas0Y[x][y] = round(0f + ktoYCbCr.get(0).get(0) * mas0R[x][y] + ktoYCbCr.get(0).get(1) * mas0G[x][y] + ktoYCbCr.get(0).get(2) * mas0B[x][y]).toShort()
                mas0Cb[x][y] = round(128 + (ktoYCbCr.get(1).get(0) * mas0R[x][y] + ktoYCbCr.get(1).get(1) * mas0G[x][y] + ktoYCbCr.get(1).get(2) * mas0B[x][y])).toShort()
                mas0Cr[x][y] = round(128 + (ktoYCbCr.get(2).get(0) * mas0R[x][y] + ktoYCbCr.get(2).get(1) * mas0G[x][y] + ktoYCbCr.get(2).get(2) * mas0B[x][y])).toShort()
                if (mas0Cr[x][y] > 255) {
                    mas0Cr[x][y] = 255
                }
                if (mas0Cr[x][y] < 0) {
                    mas0Cr[x][y] = 0
                }
                if (mas0Cb[x][y] > 255) {
                    mas0Cb[x][y] = 255
                }
                if (mas0Cb[x][y] < 0) {
                    mas0Cb[x][y] = 0
                }
                if (mas0Y[x][y] > 255) {
                    mas0Y[x][y] = 255
                }
                if (mas0Y[x][y] < 0) {
                    mas0Y[x][y] = 0
                }
            }
        }
    }
}