package features.opc.utils

import data_model.generics.matrix.Matrix
import data_model.types.DataOpc
import data_model.types.Size
import features.opc.utils.algorithms.OpcDefaultAlgorithms
import utils.MatrixUtils

class OpcProcessingUnit(
        val childSize: Size = Size(8, 8)
) {

    fun direct(image: Matrix<Short>): Matrix<out DataOpc> {
        val splitIterator = MatrixUtils.splitIterator(image, childSize, 0)
        val data = splitIterator.map { i, j, value -> value to DataOpc.Builder(childSize) }
        // todo opc steps
        val res = data.map { i, j, value -> OpcDefaultAlgorithms.direct(value.first, value.second) }
        return res
    }

    fun reverse(dataOpcMatrix: Matrix<out DataOpc>): Matrix<Short> {
        TODO()
    }
}