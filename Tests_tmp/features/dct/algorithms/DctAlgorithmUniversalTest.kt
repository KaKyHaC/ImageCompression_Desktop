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

    @Test
    fun test16() {
        test(Size(16,16))
    }

    @Test
    fun test4() {
        test(Size(4,4))
    }

    fun test(size:Size) {
        val origin = Matrix.create(size) { i, j -> rand.nextInt(500).toShort() }
        println("origin = $origin")
        val dctAlgorithmUniversal = DctAlgorithmUniversal(size)
        val direct = dctAlgorithmUniversal.direct(origin)
        println("direct = $direct")
        val reverse = dctAlgorithmUniversal.reverse(direct)
        println("reverse = $reverse")

        MatrixUtilsTest.assertMatrixInRange(origin, reverse, 0..2)
    }
}