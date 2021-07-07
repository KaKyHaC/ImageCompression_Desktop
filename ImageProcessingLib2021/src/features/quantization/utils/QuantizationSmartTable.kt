package features.quantization.utils

import data_model.generics.matrix.Matrix
import data_model.types.Size

class QuantizationSmartTable(val coefficient: Double, val size: Size) {
    val table = Matrix.create(size) { i, j -> (1 + coefficient * (i + j)) }
}