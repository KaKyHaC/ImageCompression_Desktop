package features.opc.managers

import data_model.generics.matrix.Matrix
import data_model.types.DataOpc
import data_model.types.Size
import features.opc.utils.DataOpcUtils
import features.opc.utils.OpcProcess
import utils.MatrixUtils

class OpcProcessingUnit(
        val childSize: Size = Size(8, 8)
) {

    fun direct(image: Matrix<Short>): Matrix<out DataOpc> {
        val splitIterator = MatrixUtils.splitIterator(image, childSize, 0)
        return splitIterator.map { i, j, value ->
            OpcProcess.directProcess(OpcProcess.PreOpcParams(), OpcProcess.OpcParams(), value)
        }
    }

    fun reverse(dataOpcMatrix: Matrix<out DataOpc>): Matrix<Short> {
        val image = DataOpcUtils.Data.createImageMatrix(dataOpcMatrix.size, childSize)
        val splitIterator = MatrixUtils.splitIterator(image, childSize, 0)
        splitIterator.applyEach { i, j, value ->
          OpcProcess.reverseApplyProcess(OpcProcess.PreOpcParams(), dataOpcMatrix[i,j], value)
            null
        }
        return image
    }
}