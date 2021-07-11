package features.dct.manager

import data_model.generics.matrix.Matrix
import data_model.types.Size
import org.junit.Test
import utils.MatrixUtils
import utils.MatrixUtilsTest
import java.util.*
import kotlin.math.absoluteValue


internal class DctManagerTest {
    val rand = Random()

    @Test
    fun testHdR0() {
        test(Size(1920, 1080), 0..0)
    }

    @Test
    fun testHdR10() {
        test(Size(1920, 1080), 0..10)
    }

    @Test
    fun testHdR5() {
        test(Size(1920, 1080), 0..5)
    }

    @Test
    fun test8R5() {
        test(Size(8, 8), 0..5)
    }

    fun test(imageSize: Size, range: IntRange) {
        val origin = Matrix.create(imageSize) { i, j -> rand.nextInt(255).absoluteValue.toShort() }
        val copy = MatrixUtils.copy(origin)
        val dctUnit = DctManager()
        val direct = dctUnit.direct(origin)
        val reverse = dctUnit.reverse(direct)
        MatrixUtilsTest.assertMatrixInRange(copy, reverse, range)
    }
}