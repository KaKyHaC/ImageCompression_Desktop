package ImageCompressionLib.Utils.Objects

import ImageCompressionLib.Data.Type.Pixel

object PixelFormatUtils : IUtils<Pixel.RGB, Pixel.YCbCr> {

    override fun direct(input: Pixel.RGB) = Pixel.YCbCr(
        y = (0.299 * input.r + 0.587 * input.g + 0.114 * input.b).toInt(),
        cb = (-0.168736 * input.r - 0.331264 * input.g + 0.5 * input.b + 128).toInt(),
        cr = (0.5 * input.r - 0.418688 * input.g - 0.081312 * input.b + 128).toInt()
    )

    override fun reverse(output: Pixel.YCbCr) = Pixel.RGB(
        r = (output.y + 1.402 * (output.cr - 128)).toInt(),
        g = (output.y - 0.344136 * (output.cb - 128) - 0.714136 * (output.cr - 128)).toInt(),
        b = (output.y + 1.772 * (output.cb - 128)).toInt()
    )
}