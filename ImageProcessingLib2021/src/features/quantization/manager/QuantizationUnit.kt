package features.quantization.manager

import data_model.generics.matrix.Matrix
import data_model.types.Size
import features.quantization.utils.QuantizationTable8x8
import features.quantization.utils.QuantizationTableExp
import features.quantization.utils.QuantizationTableSmart

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
        is TableType.EXPONENTIAL -> QuantizationTableExp(parameters.childSize, parameters.tableType.maxValue).table
        is TableType.SMART -> QuantizationTableSmart(parameters.tableType.coefficient, parameters.childSize).table
        is TableType.CHROMATICITY -> QuantizationTable8x8.getChromaticityMatrix()
        is TableType.LUMINOSITY -> QuantizationTable8x8.getLuminosityMatrix()
    }

    fun direct(origin: Matrix<Short>) = origin.applyEach { i, j, value ->
        (value / table[i, j]).toShort()
    }

    fun reverse(matrix: Matrix<Short>) = matrix.applyEach { i, j, value ->
        (value * table[i, j]).toShort()
    }
}