package features.image_format.data

import data_model.generics.matrix.Matrix

data class RGB(
        val r: Matrix<Short>,
        val g: Matrix<Short>,
        val b: Matrix<Short>
)
