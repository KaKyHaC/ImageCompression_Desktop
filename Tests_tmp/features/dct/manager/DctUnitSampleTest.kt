package features.dct.manager

import data_model.generics.matrix.Matrix
import data_model.types.Size
import features.dct.table.CosineTableSample
import org.junit.Test
import utils.MatrixUtilsTest
import java.util.*
import kotlin.math.absoluteValue


internal class DctUnitSampleTest {

    val rand = Random()

    @Test
    fun test8R5() {
        test(DctUnit.Parameters(), Size(8, 8), 0..5)
    }

    fun test(parameters: DctUnit.Parameters, imageSize: Size, range: IntRange) {
        val origin = Array(imageSize.width) { FloatArray(imageSize.height) { rand.nextInt(255).absoluteValue.toFloat() } }
        val table = CosineTableSample(parameters.childSize.width)
        val dctUnitSample = DctUnitSample(
                imageSize, parameters.childSize.width, parameters.childSize.width, table.dct, table.dctT, origin
        )
        val direct = dctUnitSample.DCTtoF(origin)
        val reverse = dctUnitSample.DCTtoS(direct)

        val matrixA = Matrix.create(imageSize) { i, j -> origin[i][j] }
        val matrixB = Matrix.create(imageSize) { i, j -> reverse[i][j] }
        MatrixUtilsTest.assertMatrixInRange(matrixA, matrixB, range)
    }
}