package features.opc2.managers

import data_model.generics.matrix.Matrix
import data_model.types.DataOpc2
import data_model.types.Size
import utils.MatrixUtils

class OpcBasesProcessingManager2(
        val parameters: Parameters = Parameters()
) {
    data class Parameters(
            val type: BaseProcessType = BaseProcessType.MAX_AND_MIN,
            val needTrans: Boolean = true,
            val params: OpcProcessingManager2.Parameters = OpcProcessingManager2.Parameters()
    )

    enum class BaseProcessType { MAX_ONLY, MAX_AND_MIN, MIN_ONLY }

    val manager = OpcProcessingManager2(parameters.params)

    fun direct(baseMatrix: Matrix<out DataOpc2.Base>): Matrix<DataOpc2> {
        val baseSize = Size(baseMatrix[0, 0].baseMax.size, if (parameters.type == BaseProcessType.MAX_AND_MIN) 2 else 1)
        val size = calculateBaseShortMatrixSize(baseMatrix.size, baseSize)
        val baseShortMatrix = when (parameters.type) {
            BaseProcessType.MAX_ONLY -> {
                Matrix.create(size) { i, j ->
                    baseMatrix[i / baseSize.width, j / baseSize.height].baseMax[i % baseSize.width]
                }
            }
            BaseProcessType.MAX_AND_MIN -> Matrix.create(size) { i, j ->
                if (j % 2 == 0) baseMatrix[i / baseSize.width, j / baseSize.height].baseMax[i % baseSize.width]
                else (baseMatrix[i / baseSize.width, j / baseSize.height] as DataOpc2.Base.MaxMin).baseMin[i % baseSize.width]
            }
            BaseProcessType.MIN_ONLY -> TODO()
        }
        val tmp = if (parameters.needTrans) MatrixUtils.trans(baseShortMatrix) else baseShortMatrix
        return manager.direct(tmp)
    }

    fun reverse(basesOpc: Matrix<DataOpc2>, baseMatrixSize: Size? = null, baseWidth: Int = 8): Matrix<DataOpc2.Base> {
        val baseSize = Size(baseWidth, if (parameters.type == BaseProcessType.MAX_AND_MIN) 2 else 1)
        val reverse = manager.reverse(basesOpc)
        val tmp = if (parameters.needTrans) MatrixUtils.trans(reverse) else reverse
        val splitIterator = MatrixUtils.splitIterator(tmp, baseSize, 0)
        val map: Matrix<DataOpc2.Base> = splitIterator.map { i, j, value ->
            when (parameters.type) {
                BaseProcessType.MAX_ONLY -> DataOpc2.Base.Max(ShortArray(value.width) { value[it, 0] })
                BaseProcessType.MAX_AND_MIN -> DataOpc2.Base.MaxMin(ShortArray(value.width) { value[it, 0] }, ShortArray(value.width) { value[it, 1] })
                BaseProcessType.MIN_ONLY -> TODO()
            }
        }
        return baseMatrixSize?.let { MatrixUtils.cropMatrix(map, it) } ?: map
    }

    fun calculateBaseShortMatrixSize(
            baseOpcMatrixSize: Size,
            baseSize: Size = Size(8, 1)
    ) = Size(baseOpcMatrixSize.width * baseSize.width, baseOpcMatrixSize.height * baseSize.height)
}
