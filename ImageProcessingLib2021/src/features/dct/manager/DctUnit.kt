package features.dct.manager

import data_model.generics.matrix.Matrix
import data_model.types.Size

class DctUnit(val parameters: Parameters) {

    data class Parameters(
            val childSize: Size = Size(8, 8)
    )

    fun direct(origin: Matrix<Short>): Matrix<Short> {
        TODO()
    }

    fun reverse(matrix: Matrix<Short>):Matrix<Short> {
        TODO()
    }
}