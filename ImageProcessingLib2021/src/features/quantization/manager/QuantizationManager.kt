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
            val childSize: Size = Size(8, 8),
            val tableType: TableType = TableType.EXPERIMENTAL
    )

    enum class TableType { EXPERIMENTAL, EXPONENTIAL, SMART, CHROMATICITY, LUMINOSITY }

    private val table = when(parameters.tableType) {
        TableType.EXPERIMENTAL -> TODO()
        TableType.EXPONENTIAL -> QuantizationExpTable(parameters.childSize).table
        TableType.SMART -> QuantizationSmartTable().table
        TableType.CHROMATICITY -> Quantization8x8Table.getChromaticityMatrix()
        TableType.LUMINOSITY -> Quantization8x8Table.getLuminosityMatrix()
    }

    fun direct(origin: Matrix<Short>): Matrix<Short> {
        val splitIterator = MatrixUtils.splitIterator(origin, parameters.childSize, 0)
//        splitIterator.applyEach { i, j, value -> }
        return MatrixUtils.gatherMatrix(splitIterator)
    }

    fun reverse(matrix: Matrix<Short>): Matrix<Short> {
        val splitIterator = MatrixUtils.splitIterator(matrix, parameters.childSize, 0)
//        splitIterator.applyEach { i, j, value -> }
        return MatrixUtils.gatherMatrix(splitIterator)
    }
}