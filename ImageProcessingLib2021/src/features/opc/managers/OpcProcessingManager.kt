package features.opc.managers

import data_model.generics.matrix.Matrix
import data_model.types.DataOpc
import data_model.types.Size
import features.opc.utils.DataOpcUtils
import features.opc.utils.OpcProcessUtils
import utils.MatrixUtils

class OpcProcessingManager(
        val parameters: Parameters = Parameters()
) {
    data class Parameters(
            val childSize: Size = Size(8, 8),
            val params: OpcProcessUnit.Parameters = OpcProcessUnit.Parameters()
    )

    val unit = OpcProcessUnit(parameters.params)

    fun direct(image: Matrix<Short>): Matrix<out DataOpc> {
        val splitIterator = MatrixUtils.splitIterator(image, parameters.childSize, 0)
        return splitIterator.map { i, j, value -> unit.direct(value) }
    }

    fun reverse(dataOpcMatrix: Matrix<out DataOpc>, imageSize: Size? = null): Matrix<Short> {
        val map = dataOpcMatrix.map { i, j, dataOpc -> unit.reverse(dataOpc, parameters.childSize) }
        return MatrixUtils.gatherMatrix(map, imageSize)
    }
}