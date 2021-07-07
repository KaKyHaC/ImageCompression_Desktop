package features.quantization.utils

import data_model.generics.matrix.Matrix
import data_model.types.Size

class QuantizationSmartTable(val coefficient: Double = 0.0, val size: Size = Size(8,8)) {
    val table = Matrix.create(size) { i, j -> (1 + coefficient * (i + j)) }
}