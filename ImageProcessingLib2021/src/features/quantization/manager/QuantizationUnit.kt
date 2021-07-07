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

class QuantizationUnit(val parameters: Parameters) {

    data class Parameters(
            val childSize: Size = Size(8, 8),
            val tableType: TableType = TableType.EXPONENTIAL(255.0)
    )

    sealed class TableType {
        object EXPERIMENTAL : TableType()
        class EXPONENTIAL(val maxValue:Double): TableType()
        class SMART(val coefficient: Double): TableType()
        object CHROMATICITY : TableType()
        object LUMINOSITY : TableType()
    }

    private val table = when (parameters.tableType) {
        is TableType.EXPERIMENTAL -> TODO()
        is TableType.EXPONENTIAL -> QuantizationExpTable(parameters.childSize, parameters.tableType.maxValue).table
        is TableType.SMART -> QuantizationSmartTable(parameters.tableType.coefficient, parameters.childSize).table
        is TableType.CHROMATICITY -> Quantization8x8Table.getChromaticityMatrix()
        is TableType.LUMINOSITY -> Quantization8x8Table.getLuminosityMatrix()
    }

    fun direct(origin: Matrix<Short>) = origin.applyEach { i, j, value ->
        (value / table[i, j]).toShort()
    }

    fun reverse(matrix: Matrix<Short>) = matrix.applyEach { i, j, value ->
        (value * table[i, j]).toShort()
    }
}