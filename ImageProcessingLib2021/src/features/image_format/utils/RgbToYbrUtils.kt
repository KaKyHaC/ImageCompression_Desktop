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
        val map = yCbCr.pixelMatrix.map { x, y, value ->
            var r = value.Y + 1.402 * (value.Cr - 128)
            var g = value.Y.toDouble() - 0.34414 * (value.Cb - 128) - 0.71414 * (value.Cr - 128)
            var b = value.Y + 1.772 * (value.Cb - 128)

            //todo
            if (g < 0) g = 0.0//new
            if (r < 0) r = 0.0
            if (b < 0) b = 0.0

            //todo
            if (r > 255) r = 255.0
            if (g > 255) g = 255.0
            if (b > 255) b = 255.0

            val pixelRed = (r.toInt() and 0xFF).toShort()
            val pixelGreen = (g.toInt() and 0xFF).toShort()
            val pixelBlue = (b.toInt() and 0xFF).toShort()

            //add
            //                if(r%1>=0.5)
            //                    pixelRed++;
            //                if(g%1>=0.5)
            //                    pixelGreen++;
            //                if(b%1>=0.5)
            //                    pixelBlue++;
            //

            RGB.Pixel(pixelRed, pixelGreen, pixelBlue)
        }
        return RGB(map)
    }
}