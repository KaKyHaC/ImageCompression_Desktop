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

    val failedPreOpc = Matrix(arrayOf(
            arrayOf(100, 56, -15, 8, -1, 0, 0, 0),
            arrayOf(280, -29, -11, -9, -3, -2, 0, 0),
            arrayOf(-84, -71, 10, -3, 0, 0, 0, 0),
            arrayOf(-10, 37, 1, -3, 0, -1, 0, 0),
            arrayOf(0, -3, -9, -1, -1, 0, 0, 0),
            arrayOf(-1, -3, 0, 2, 0, 1, 0, 0),
            arrayOf(1, 0, 2, -1, -1, -1, 0, 0),
            arrayOf(-1, 0, -1, -2, 2, 0, 0, 0)
    ), Int::class).map { i, j, value -> value.toShort() }

    @Test
    fun test8R45LUMINOSITY() {
        for ( i in 0..10000)
        testMax(Size(8), 0..45, QuantizationUnit.TableType.LUMINOSITY(true))
    }


    @Test
    fun test8R65CHROMATICITY() {
        for ( i in 0..10000)
            testMax(Size(8), 0..65, QuantizationUnit.TableType.CHROMATICITY(true))
    }


    @Test
    fun test8R65EXPONENTIAL() {
        for ( i in 0..10000)
            testMax(Size(8), 0..65, QuantizationUnit.TableType.EXPONENTIAL(255.0))
    }

    @Test
    fun failedOpc() {
        val dctUnit = DctManager()
        val quantizationUnit = QuantizationUnit(QuantizationUnit.Parameters(tableType = QuantizationUnit.TableType.LUMINOSITY(true)))
        val reverse = quantizationUnit.reverse(failedPreOpc)
        val reverse1 = dctUnit.reverse(reverse)
        println(reverse1)
    }

    fun testMax(imageSize: Size, range: IntRange, quantizationType: QuantizationUnit.TableType) {
        val origin = Matrix.create(imageSize) { i, j -> rand.nextInt(255).absoluteValue.toShort() }
        val dctUnit = DctManager() // todo use manager
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

        MatrixUtilsTest.assertMatrixInRange(origin, reverseDct, range)
        quant.applyEach { i, j, value -> if (value > 255) throw Exception() else null }
    }
}