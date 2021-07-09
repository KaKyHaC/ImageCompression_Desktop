package features.read_image.utils

import data_model.generics.Triple
import data_model.generics.matrix.Matrix
import data_model.processing_data.ProcessingData
import data_model.types.Size
import org.junit.Test
import java.util.*
import kotlin.math.absoluteValue
import kotlin.test.assertEquals


internal class ReadBmpUtilsTest {

    val rand = Random()

    @Test
    fun testHd() {
        test(Size(1920, 1080))
    }

    @Test
    fun test8() {
        test(Size(8))
    }

    fun test(size: Size) {
        val data = Triple(
                Matrix.create(size) { i, j -> rand.nextInt(255).absoluteValue.toShort() },
                Matrix.create(size) { i, j -> rand.nextInt(255).absoluteValue.toShort() },
                Matrix.create(size) { i, j -> rand.nextInt(255).absoluteValue.toShort() }
        )
        val bufferedImage = ReadBmpUtils.map(data)
        val map = ReadBmpUtils.map(bufferedImage)
        assertEquals(data, map)
    }
}