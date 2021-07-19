package features.image_format.utils

import data_model.generics.matrix.Matrix
import data_model.types.Size
import features.image_format.data.RGB
import org.junit.Test
import java.util.*
import kotlin.math.absoluteValue
import kotlin.test.assertTrue


internal class RgbToYbrUtilsTest {

    val random = Random()

    @Test
    fun test128() {
        test(Size(128, 128), 0..0)
    }

    @Test
    fun test128R2() {
        test(Size(128, 128), 0..2)
    }

    @Test
    fun test128R5() {
        test(Size(128, 128), 0..5)
    }

    @Test
    fun testHdR5() {
        test(Size(1920, 1080), 0..5)
    }

    @Test
    fun test4KR5() {
        test(Size(4000, 2000), 0..5)
    }

    fun test(size: Size, range: IntRange) {
        val origin = RGB(Matrix.create(size) { i, j ->
            val red = (random.nextInt(255)).toShort()
            val green = (random.nextInt(255)).toShort()
            val blue = (random.nextInt(255)).toShort()
            RGB.Pixel(red, green, blue)
        })
        val ybr = RgbToYbrUtils.direct(origin)
        val reverse = RgbToYbrUtils.reverse(ybr)

        var maxY:Short = 0
        var maxB:Short = 0
        var maxR:Short = 0
        ybr.pixelMatrix.applyEach { i, j, value ->
            if (value.Y > maxY) maxY = value.Y
            if (value.Cb > maxB) maxB = value.Cb
            if (value.Cr > maxR) maxR = value.Cr
            null
        }
        println("maxY = $maxY, maxB = $maxB, maxR = $maxR")

        origin.pixelMatrix.applyEach { i, j, value ->
            val reversePixel = reverse.pixelMatrix[i, j]
            val dR = (value.red - reversePixel.red).absoluteValue
            val dG = (value.green - reversePixel.green).absoluteValue
            val dB = (value.blue - reversePixel.blue).absoluteValue
            assertTrue("for [$i,$j] r = ${value.red} new = ${reversePixel.red}") { dR in range }
            assertTrue("for [$i,$j] g = ${value.green} new = ${reversePixel.green}") { dG in range }
            assertTrue("for [$i,$j] b = ${value.blue} new = ${reversePixel.blue}") { dB in range }
            null
        }
    }
}