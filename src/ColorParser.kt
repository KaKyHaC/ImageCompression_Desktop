import ImageCompression.Objects.MyImage
import java.awt.Color

class ColorParser : MyImage.Color {
    override fun red(pixel: Int): Double {
        return Color(pixel).red as Double
    }

    override fun blue(pixel: Int): Double {
        return Color(pixel).blue as Double

    }

    override fun green(pixel: Int): Double {
        return Color(pixel).green as Double

    }

    override fun argb(pixelAlpha: Int, pixelRed: Int, pixelGreen: Int, pixelBlue: Int): Int {
        return Color(pixelRed,pixelGreen,pixelBlue,pixelAlpha).rgb
    }
}