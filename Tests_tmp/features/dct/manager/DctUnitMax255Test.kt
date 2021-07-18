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


    val failedPreQuant = Matrix(arrayOf(
            arrayOf(402, 170, -46, 25, -6, 4, 6, -6),
            arrayOf(560, -87, -34, -37, -19, -20, -12, 0),
            arrayOf(-168, -215, 41, -17, -3, 7, 0, 3),
            arrayOf(-41, 151, 9, -22, 4, -22, 0, -8),
            arrayOf(0, -20, -87, -18, -18, -3, -11, 0),
            arrayOf(-14, -44, -9, 41, -7, 23, 1, 0),
            arrayOf(20, -4, 40, -27, -33, -30, -10, 0),
            arrayOf(-17, -10, -22, -30, 42, 11, 2, 2)
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

    @Test
    fun failedQuant() {
        val dctUnit = DctManager()
        val reverse1 = dctUnit.reverse(failedPreQuant)
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