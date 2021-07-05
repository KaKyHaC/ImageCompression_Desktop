package features.image_format.data

import data_model.generics.matrix.Matrix

data class YCbCr(
        val pixelMatrix: Matrix<Pixel>
) {
    constructor(
            Y: Matrix<Short>,
            Cb: Matrix<Short>,
            Cr: Matrix<Short>
    ) : this(Matrix.create(Y.size) { i, j -> Pixel(Y[i, j], Cb[i, j], Cr[i, j]) })

    data class Pixel(
            val Y: Short,
            val Cb: Short,
            val Cr: Short
    )
}
