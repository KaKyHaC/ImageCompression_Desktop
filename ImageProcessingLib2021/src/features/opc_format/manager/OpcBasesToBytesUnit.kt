package features.opc_format.manager

import data_model.generics.matrix.Matrix
import data_model.types.ByteVector
import data_model.types.DataOpc2
import data_model.types.Size
import features.opc_format.utils.ByteVectorUtils

class OpcBasesToBytesUnit(
        val parameters: Parameters = Parameters()
) {

    data class Parameters(
            val type: ByteVectorUtils.Bases.Type = ByteVectorUtils.Bases.Type.MAX,
            val baseType: BaseType = BaseType.BYTE
    )

    enum class BaseType { BYTE, SHORT }

    fun direct(
            byteVector: ByteVector,
            basesMatrix: Matrix<DataOpc2.Base>
    ) {
        if (parameters.baseType == BaseType.BYTE)
            basesMatrix.applyEach { i, j, value ->
                ByteVectorUtils.Bases.direct(byteVector, value, parameters.type)
                null
            }
        else if (parameters.baseType == BaseType.SHORT) {
            basesMatrix.applyEach { i, j, value ->
                ByteVectorUtils.Bases.directShort(byteVector, value, parameters.type)
                null
            }
        }
    }

    fun reverse(reader: ByteVector.Read, baseSize: Int, matrixSize: Size): Matrix<DataOpc2.Base> {
        return if (parameters.baseType == BaseType.BYTE)
            Matrix.create(matrixSize) { i, j -> ByteVectorUtils.Bases.reverse(reader, baseSize, parameters.type) }
        else
            Matrix.create(matrixSize) { i, j -> ByteVectorUtils.Bases.reverseShort(reader, baseSize, parameters.type) }
    }
}