package features.quantization.utils

import data_model.generics.matrix.Matrix
import data_model.types.Size


class QuantizationExpTable(
        val size: Size,
        val maxValue: Double
) {
    val table = Matrix.create(size) { i, j ->
        val r = Math.exp((i + j) / 2.0)
        if (r > maxValue) maxValue else r
    }

    operator fun get(i: Int, j: Int) = table[i,j]
}