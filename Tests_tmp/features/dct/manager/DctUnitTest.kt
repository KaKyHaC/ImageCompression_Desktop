package features.dct.manager

import data_model.generics.matrix.Matrix
import data_model.types.Size
import org.junit.Test
import utils.MatrixUtilsTest
import java.util.*
import kotlin.math.absoluteValue


internal class DctUnitTest {
    val rand = Random()

    @Test
    fun testHdR0() {
        test(DctUnit.Parameters(), Size(1920,1080), 0..0)
    }

    @Test
    fun testHdR2() {
        test(DctUnit.Parameters(), Size(1920,1080), 0..2)
    }

    @Test
    fun testHdR5() {
        test(DctUnit.Parameters(), Size(1920,1080), 0..5)
    }

    fun test(parameters: DctUnit.Parameters, imageSize: Size, range: IntRange) {
        val origin = Matrix.create(imageSize) { i, j -> rand.nextInt(255).absoluteValue.toShort() }
        val dctUnit = DctUnit(parameters)
        val direct = dctUnit.direct(origin)
        val reverse = dctUnit.reverse(direct)
        MatrixUtilsTest.assertMatrixInRange(origin, reverse, range)
    }
}