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
            arrayOf(-48, -619, 257, 77, -69, -16, 5, 15),
            arrayOf(-14, 86, 85, -163, 46, 84, -34, -7),
            arrayOf(32, 65, -65, -68, 59, -62, -43, 71),
            arrayOf(0, 28, 0, 4, 25, -19, 8, -6),
            arrayOf(15, 28, 1, 6, 8, -1, 27, -27),
            arrayOf(-8, 0, 0, 22, 3, -13, 14, -11),
            arrayOf(-5, -13, -18, 2, -2, -13, 13, -10),
            arrayOf(-8, -11, 0, 10, 0, 0, 11, -13)
    ), Int::class).map { i, j, value -> value.toShort() }.let { MatrixUtils.trans(it)}


    val failedOrigin = Matrix(arrayOf(
            arrayOf(66, 67, 68, 69, 76, 57, 160, 253),
            arrayOf(49, 56, 65, 53, 60, 52, 242, 253),
            arrayOf(30, 24, 24, 27, 38, 159, 251, 253),
            arrayOf(76, 38, 20, 24, 36, 215, 254, 250),
            arrayOf(80, 41, 16, 28, 65, 252, 253, 255),
            arrayOf(55, 56, 29, 22, 144, 252, 253, 253),
            arrayOf(62, 70, 37, 21, 193, 253, 254, 251),
            arrayOf(52, 86, 73, 49, 226, 254, 249, 254)
    ), Int::class).map { i, j, value -> value.toShort() }.let { MatrixUtils.trans(it)}

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

    @Test
    fun test8R45LUMINOSITY() {
        for (i in 0..10000)
            testMax(Size(8), 0..45, QuantizationUnit.TableType.LUMINOSITY(true))
    }


    @Test
    fun test8R65CHROMATICITY() {
        for (i in 0..10000)
            testMax(Size(8), 0..65, QuantizationUnit.TableType.CHROMATICITY(true))
    }


    @Test
    fun test8R65EXPONENTIAL() {
        for (i in 0..10000)
            testMax(Size(8), 0..65, QuantizationUnit.TableType.EXPONENTIAL(255.0))
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
        quant.applyEach { i, j, value -> if (value.toInt().absoluteValue > 255) throw Exception() else null }
    }

    @Test
    fun testMax() {
        val origin = failedOrigin
        val dctUnit = DctManager() // todo use manager
        val quantizationUnit = QuantizationUnit(QuantizationUnit.Parameters(origin.size, QuantizationUnit.TableType.LUMINOSITY(true)))

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