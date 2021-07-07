package features.opc2.managers

import data_model.generics.matrix.Matrix
import data_model.types.DataOpc2
import data_model.types.Size
import utils.MatrixUtils

class OpcBasesProcessingManager2(
        val parameters: Parameters = Parameters()
) {
    data class Parameters(
            val type: BaseProcessType = BaseProcessType.MAX_ONLY,
            val params: OpcProcessingManager2.Parameters = OpcProcessingManager2.Parameters()
    )

    enum class BaseProcessType { MAX_ONLY, MAX_AND_MIN, MIN_ONLY }

    val manager = OpcProcessingManager2(parameters.params)

    fun direct(baseMatrix: Matrix<DataOpc2.Base>): Matrix<out DataOpc2> {
        val baseSize = Size(baseMatrix[0, 0].baseMax.size, 1)
        val baseShortMatrix = when (parameters.type) {
            BaseProcessType.MAX_ONLY -> Matrix.create(Size(baseMatrix.width * baseSize.width, baseMatrix.height * baseSize.height)) { i, j ->
                baseMatrix[i / baseSize.width, j / baseSize.height].baseMax[i % baseSize.width]
            }
            BaseProcessType.MAX_AND_MIN -> TODO()
            BaseProcessType.MIN_ONLY -> TODO()
        }
        return manager.direct(baseShortMatrix)
    }

    fun reverse(basesOpc: Matrix<DataOpc2>): Matrix<DataOpc2.Base> {
        TODO()
    }
}