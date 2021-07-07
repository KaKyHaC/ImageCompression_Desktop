package features.quantization.manager

import data_model.generics.matrix.Matrix
import utils.MatrixUtils

class QuantizationManager(val parameters: Parameters) {

    data class Parameters(
            val parameters: QuantizationUnit.Parameters = QuantizationUnit.Parameters()
    ) {
        val childSize = parameters.childSize
    }

    private val unit = QuantizationUnit(parameters.parameters)

    fun direct(origin: Matrix<Short>): Matrix<Short> {
        val splitIterator = MatrixUtils.splitIterator(origin, parameters.childSize, 0)
        splitIterator.applyEach { i, j, value -> unit.direct(value) }
        return MatrixUtils.gatherMatrix(splitIterator)
    }

    fun reverse(matrix: Matrix<Short>): Matrix<Short> {
        val splitIterator = MatrixUtils.splitIterator(matrix, parameters.childSize, 0)
        splitIterator.applyEach { i, j, value -> unit.reverse(value) }
        return MatrixUtils.gatherMatrix(splitIterator)
    }
}