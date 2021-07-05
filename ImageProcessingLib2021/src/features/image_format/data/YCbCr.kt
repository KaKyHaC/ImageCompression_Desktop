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

    val matrixY by lazy { Matrix.create(pixelMatrix.size) { i, j -> pixelMatrix[i, j].Y } }
    val matrixCb by lazy { Matrix.create(pixelMatrix.size) { i, j -> pixelMatrix[i, j].Cb } }
    val matrixCr by lazy { Matrix.create(pixelMatrix.size) { i, j -> pixelMatrix[i, j].Cr } }

    data class Pixel(
            val Y: Short,
            val Cb: Short,
            val Cr: Short
    )
}
