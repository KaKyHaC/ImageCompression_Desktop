package features.dct.manager

import data_model.generics.matrix.Matrix
import data_model.types.Size
import features.dct.algorithms.DctAlgorithmExperimental
import features.dct.algorithms.DctAlgorithmUniversal
import features.dct.utils.CosineTableFactory

class DctUnit(val parameters: Parameters) {

    data class Parameters(
            val childSize: Size = Size(8, 8),
            val algorithmType: AlgorithmType = AlgorithmType.UNIVERSAL
    )

    enum class AlgorithmType { EXPERIMENTAL, UNIVERSAL }

    val algorithmExperimental = CosineTableFactory.getExperimentalTable(parameters.childSize.width)
            .let { DctAlgorithmExperimental(it.dct, it.dctT) }

    val dctAlgorithmUniversal = DctAlgorithmUniversal(parameters.childSize)


    fun direct(origin: Matrix<Short>): Matrix<Short> {
        return when(parameters.algorithmType) {
            AlgorithmType.EXPERIMENTAL -> algorithmExperimental.direct(origin, Double::toShort)
            AlgorithmType.UNIVERSAL -> dctAlgorithmUniversal.direct(origin)
        }
    }

    fun reverse(matrix: Matrix<Short>): Matrix<Short> {
        return when(parameters.algorithmType) {
            AlgorithmType.EXPERIMENTAL -> algorithmExperimental.reverse(matrix, Double::toShort)
            AlgorithmType.UNIVERSAL -> dctAlgorithmUniversal.reverse(matrix)
        }
    }
}