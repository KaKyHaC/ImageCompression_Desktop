package features.dct.manager

import data_model.generics.matrix.Matrix
import data_model.types.Size
import features.dct.algorithms.DctAlgorithmExperimental
import features.dct.utils.CosineTableFactory

class DctUnit(val parameters: Parameters) {

    data class Parameters(
            val childSize: Size = Size(8, 8)
    )

    val experimentalTable = CosineTableFactory.getExperimentalTable(parameters.childSize.width)
    val algorithmExperimental = DctAlgorithmExperimental(experimentalTable.dct, experimentalTable.dctT)

    fun direct(origin: Matrix<Short>): Matrix<Short> {
        return algorithmExperimental.direct(origin, Double::toShort)
    }

    fun reverse(matrix: Matrix<Short>): Matrix<Short> {
        return algorithmExperimental.reverse(matrix, Double::toShort)
    }
}