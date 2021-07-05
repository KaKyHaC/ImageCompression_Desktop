package features.image_format.data

import data_model.generics.matrix.Matrix

data class RGB(
        val pixelMatrix: Matrix<Pixel>
) {
    constructor(
            r: Matrix<Short>, g: Matrix<Short>, b: Matrix<Short>
    ) : this(Matrix.create(r.size) { i, j -> Pixel(r[i, j], g[i, j], b[i, j]) })

    data class Pixel(val red: Short, val green: Short, val blue: Short)
}
