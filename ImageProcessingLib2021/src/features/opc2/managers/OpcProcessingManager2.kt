package features.opc2.managers

import data_model.generics.matrix.Matrix
import data_model.types.DataOpc
import data_model.types.DataOpc2
import data_model.types.Size
import utils.MatrixUtils

class OpcProcessingManager2(
        val parameters: Parameters = Parameters()
) {
    data class Parameters(
            val childSize: Size = Size(8, 8),
            val params: OpcProcessUnit2.Parameters = OpcProcessUnit2.Parameters()
    )

    val unit = OpcProcessUnit2(parameters.params)

    fun direct(image: Matrix<Short>): Matrix<out DataOpc2> {
        val splitIterator = MatrixUtils.splitIterator(image, parameters.childSize, 0)
        return splitIterator.map { i, j, value -> unit.direct(value) }
    }

    fun reverse(dataOpcMatrix: Matrix<out DataOpc2>, imageSize: Size? = null): Matrix<Short> {
        val map = dataOpcMatrix.map { i, j, dataOpc -> unit.reverse(dataOpc, parameters.childSize) }
        return MatrixUtils.gatherMatrix(map, imageSize)
    }
}