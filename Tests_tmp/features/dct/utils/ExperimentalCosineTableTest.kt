package features.dct.utils

import data_model.generics.matrix.Matrix
import org.junit.Test
import utils.MatrixUtilsTest


internal class ExperimentalCosineTableTest {

    @Test
    fun test8() {
        test(8)
    }

    @Test
    fun test16() {
        test(16)
    }

    fun test(size: Int) {
        val experimentalCosineTable = ExperimentalCosineTable(size)
        val sample = CosineTableSample(size)
        val dct = Matrix.create(experimentalCosineTable.dct.size) { i, j -> sample.dct[i][j] }
        val dctT = Matrix.create(experimentalCosineTable.dctT.size) { i, j -> sample.dctT[i][j] }
        MatrixUtilsTest.assertMatrixInRange(experimentalCosineTable.dct, dct, 0..0)
        MatrixUtilsTest.assertMatrixInRange(experimentalCosineTable.dctT, dctT, 0..0)
    }
}