package features.quantization.utils

import data_model.generics.matrix.Matrix

object QuantizationTable2021 {

    private val yMatrix = Matrix(arrayOf(
            arrayOf(4, 3, 3, 4, 5, 9, 11, 13),
            arrayOf(3, 3, 3, 4, 6, 13, 13, 12),
            arrayOf(3, 3, 4, 5, 9, 13, 15, 12),
            arrayOf(3, 4, 5, 6, 11, 19, 18, 14),
            arrayOf(4, 5, 8, 12, 15, 24, 23, 17),
            arrayOf(5, 8, 12, 14, 18, 23, 25, 20),
            arrayOf(11, 14, 17, 19, 23, 27, 26, 22),
            arrayOf(16, 20, 21, 22, 25, 22, 23, 22)
    ), Int::class)

    private val cbcrMatrix = Matrix(arrayOf(
            arrayOf(4, 4, 5, 10, 22, 22, 22, 22),
            arrayOf(4, 5, 6, 15, 22, 22, 22, 22),
            arrayOf(5, 6, 12, 22, 22, 22, 22, 22),
            arrayOf(10, 15, 22, 22, 22, 22, 22, 22),
            arrayOf(22, 22, 22, 22, 22, 22, 22, 22),
            arrayOf(22, 22, 22, 22, 22, 22, 22, 22),
            arrayOf(22, 22, 22, 22, 22, 22, 22, 22),
            arrayOf(22, 22, 22, 22, 22, 22, 22, 22)
    ), Int::class)


    fun getLuminosityMatrix() = yMatrix.map { i, j, value -> value.toDouble() }

    fun getChromaticityMatrix() = cbcrMatrix.map { i, j, value -> value.toDouble() }

}