package ImageCompressionLib.Data.Primitives

abstract class Pixel {
    data class RGB(
        val r: Int,
        val g: Int,
        val b: Int
    ) : Pixel()

    data class YCbCr(
        val y: Int,
        val cb: Int,
        val cr: Int
    ) : Pixel()
}