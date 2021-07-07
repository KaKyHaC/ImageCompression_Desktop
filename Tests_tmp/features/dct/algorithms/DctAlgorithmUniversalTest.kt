package features.dct.algorithms

import data_model.generics.matrix.Matrix
import data_model.types.Size
import org.junit.Test
import utils.MatrixUtilsTest
import java.util.*


internal class DctAlgorithmUniversalTest {

    val rand = Random()

    @Test
    fun test10() {
        test(Size(10,10))
    }

    @Test
    fun test8() {
        test(Size(8,8))
    }

    fun test(size:Size) {
        val origin = Matrix.create(size) { i, j -> rand.nextInt(500).toShort() }
        val dctAlgorithmUniversal = DctAlgorithmUniversal(size)
        val direct = dctAlgorithmUniversal.direct(origin)
        val reverse = dctAlgorithmUniversal.reverse(direct)
        MatrixUtilsTest.assertMatrixInRange(origin, reverse, 0..2)
    }
}