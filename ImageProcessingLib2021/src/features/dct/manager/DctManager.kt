package features.dct.manager

import data_model.generics.matrix.Matrix
import data_model.types.Size
import features.dct.utils.DctUtils
import utils.MatrixUtils

class DctManager(val parameters: Parameters) {

    data class Parameters(
            val childSize: Size = Size(8, 8),
            val params: DctUnit.Parameters = DctUnit.Parameters(),
            val subtractFirstElements: Boolean = true,
            val remove128: Boolean = true
    )

    val unit = DctUnit(parameters.params)

    fun direct(origin: Matrix<Short>): Matrix<Short> {
        val minus128 = if (parameters.remove128)  DctUtils.minus128(origin) else origin
        val splitIterator = MatrixUtils.splitIterator(minus128, parameters.childSize, 0)
        if (parameters.subtractFirstElements) DctUtils.subtractFirstElements(splitIterator)
        splitIterator.applyEach { i, j, value -> unit.direct(value) }
        return MatrixUtils.gatherMatrix(splitIterator)
    }

    fun reverse(matrix: Matrix<Short>): Matrix<Short> {
        val splitIterator = MatrixUtils.splitIterator(matrix, parameters.childSize, 0)
        if (parameters.subtractFirstElements) DctUtils.subtractFirstElements(splitIterator)
        splitIterator.applyEach { i, j, value -> unit.reverse(value) }
        val gatherMatrix = MatrixUtils.gatherMatrix(splitIterator)
        val res = if (parameters.remove128)  DctUtils.plus128(gatherMatrix) else gatherMatrix
        return res
    }
}