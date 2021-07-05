package features.image_format.utils

import data_model.generics.matrix.Matrix
import features.image_format.data.RGB
import features.image_format.data.YCbCr

object RgbToYbrUtils {

    fun direct(rgb: RGB): YCbCr {
        val map = rgb.pixelMatrix.map { x, y, value ->
            val pixelRed = value.red.toDouble()
            val pixelGreen = value.green.toDouble()
            val pixelBlue = value.blue.toDouble()

            val vy = 0.299 * pixelRed + 0.587 * pixelGreen + 0.114 * pixelBlue
            val vcb = 128.0 - 0.168736 * pixelRed - 0.331264 * pixelGreen + 0.5 * pixelBlue
            val vcr = 128 + 0.5 * pixelRed - 0.418688 * pixelGreen - 0.081312 * pixelBlue

            //                   if(vy%1>=0.5)
            //                       vy++;
            //                   if(vcb%1>=0.5)
            //                       vcb++;
            //                   if(vcr%1>=0.5)
            //                       vcr++;

            YCbCr.Pixel(vy.toShort(), vcb.toShort(), vcr.toShort())
        }
        return YCbCr(map)
    }

    fun reverse(yCbCr: YCbCr): RGB {
        TODO()
    }
}