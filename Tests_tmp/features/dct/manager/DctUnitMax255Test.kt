package features.dct.manager

import ImageCompressionLib.constants.QuantizationTable
import data_model.generics.matrix.Matrix
import data_model.types.Size
import features.quantization.manager.QuantizationUnit
import org.junit.Test
import utils.MatrixUtils
import utils.MatrixUtilsTest
import java.util.*
import kotlin.math.absoluteValue


internal class DctUnitMax255Test {
    val rand = Random()


    @Test
    fun test8R5() {
        testMax(Size(8), 0..5, QuantizationUnit.TableType.LUMINOSITY(true))
    }

    fun testMax(imageSize: Size, range: IntRange, quantizationType: QuantizationUnit.TableType) {
        val origin = Matrix.create(imageSize) { i, j -> rand.nextInt(255).absoluteValue.toShort() }
        val dctUnit = DctUnit(DctUnit.Parameters(imageSize))
        val quantizationUnit = QuantizationUnit(QuantizationUnit.Parameters(imageSize, quantizationType))

        val dct = dctUnit.direct(MatrixUtils.copy(origin))
        val quant = quantizationUnit.direct(MatrixUtils.copy(dct))
        val reverseQunt = quantizationUnit.reverse(MatrixUtils.copy(quant))
        val reverseDct = dctUnit.reverse(MatrixUtils.copy(reverseQunt))


        println("origin = $origin")
        println("dct = $dct")
        println("quant = $quant")
        println("reverseQunt = $reverseQunt")
        println("reverseDct = $reverseDct")

//        MatrixUtilsTest.assertMatrixInRange(origin, reverseDct, range)
        quant.applyEach { i, j, value -> if (value > 255) throw Exception() else null }
    }
}