package features.opc.managers

import data_model.generics.matrix.Matrix
import data_model.types.DataOpc
import data_model.types.Size
import features.opc.utils.DataOpcUtils
import features.opc.utils.OpcProcessUtils
import utils.MatrixUtils

class OpcProcessingUnit(
        val parameters: Parameters = Parameters()
) {
    data class Parameters(
            val childSize: Size = Size(8, 8),
            val preOpcParams: OpcProcessUtils.PreOpcParams = OpcProcessUtils.PreOpcParams(),
            val opcParams: OpcProcessUtils.OpcParams = OpcProcessUtils.OpcParams()
    )

    fun direct(image: Matrix<Short>): Matrix<out DataOpc> {
        val splitIterator = MatrixUtils.splitIterator(image, parameters.childSize, 0)
        return splitIterator.map { i, j, value ->
            OpcProcessUtils.directProcess(parameters.preOpcParams, parameters.opcParams, value)
        }
    }

    fun reverse(dataOpcMatrix: Matrix<out DataOpc>, imageSize: Size? = null): Matrix<Short> {
        val image = DataOpcUtils.Data.createImageMatrix(dataOpcMatrix.size, parameters.childSize)
        val splitIterator = MatrixUtils.splitIterator(image, parameters.childSize, 0)
        splitIterator.applyEach { i, j, value ->
            OpcProcessUtils.reverseApplyProcess(parameters.preOpcParams, dataOpcMatrix[i, j], value)
            null
        }
        return imageSize?.let { MatrixUtils.cropMatrix(image, it) } ?: image
    }
}