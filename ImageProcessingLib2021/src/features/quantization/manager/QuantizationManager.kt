package features.quantization.manager

import data_model.generics.matrix.Matrix
import data_model.types.Size
import features.dct.algorithms.ExperimentalDctAlgorithm
import features.dct.utils.CosineTableFactory
import features.dct.utils.DctUtils
import features.quantization.utils.Quantization8x8Table
import features.quantization.utils.QuantizationExpTable
import features.quantization.utils.QuantizationSmartTable
import utils.MatrixUtils

class QuantizationManager(val parameters: Parameters) {

    data class Parameters(
            val parameters: QuantizationUnit.Parameters = QuantizationUnit.Parameters()
    ) {
        val childSize = parameters.childSize
    }

    private val unit = QuantizationUnit(parameters.parameters)

    fun direct(origin: Matrix<Short>): Matrix<Short> {
        val splitIterator = MatrixUtils.splitIterator(origin, parameters.childSize, 0)
        splitIterator.applyEach { i, j, value -> unit.direct(value) }
        return MatrixUtils.gatherMatrix(splitIterator)
    }

    fun reverse(matrix: Matrix<Short>): Matrix<Short> {
        val splitIterator = MatrixUtils.splitIterator(matrix, parameters.childSize, 0)
        splitIterator.applyEach { i, j, value -> unit.reverse(value) }
        return MatrixUtils.gatherMatrix(splitIterator)
    }
}