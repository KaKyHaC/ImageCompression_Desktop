package features.image_format.data

import data_model.generics.matrix.Matrix

data class YCbCr(
        val Y: Matrix<Short>,
        val Cb: Matrix<Short>,
        val Cr: Matrix<Short>
)
