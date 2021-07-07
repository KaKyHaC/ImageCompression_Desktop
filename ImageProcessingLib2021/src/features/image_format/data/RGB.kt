package features.image_format.data

import data_model.generics.matrix.Matrix

data class RGB(
        val pixelMatrix: Matrix<Pixel>
) {
    constructor(
            r: Matrix<Short>, g: Matrix<Short>, b: Matrix<Short>
    ) : this(Matrix.create(r.size) { i, j -> Pixel(r[i, j], g[i, j], b[i, j]) })

    val matrixR by lazy { Matrix.create(pixelMatrix.size) { i, j -> pixelMatrix[i, j].red } }
    val matrixG by lazy { Matrix.create(pixelMatrix.size) { i, j -> pixelMatrix[i, j].green } }
    val matrixB by lazy { Matrix.create(pixelMatrix.size) { i, j -> pixelMatrix[i, j].blue } }

    data class Pixel(val red: Short, val green: Short, val blue: Short)
}
