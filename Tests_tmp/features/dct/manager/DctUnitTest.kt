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
    fun test32R5() {
        test(DctUnit.Parameters(), Size(32,32), 0..5)
    }

    @Test
    fun test16R5() {
        test(DctUnit.Parameters(), Size(16), 0..5)
    }

    @Test
    fun test8R5() {
        test(DctUnit.Parameters(), Size(8), 0..5)
    }

    fun test(parameters: DctUnit.Parameters, imageSize: Size, range: IntRange) {
        val origin = Matrix.create(imageSize) { i, j -> rand.nextInt(255).absoluteValue.toShort() }
        val dctUnit = DctUnit(parameters)
        val direct = dctUnit.direct(origin)
        val reverse = dctUnit.reverse(direct)
        MatrixUtilsTest.assertMatrixInRange(origin, reverse, range)
    }
}