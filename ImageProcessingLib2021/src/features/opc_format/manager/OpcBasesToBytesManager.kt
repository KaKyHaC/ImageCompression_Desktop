package features.opc_format.manager

import data_model.generics.matrix.Matrix
import data_model.types.ByteVector
import data_model.types.DataOpc2
import data_model.types.Size
import features.opc_format.utils.OpcBasesToBytesUtils
import kotlin.experimental.and

class OpcBasesToBytesManager(
        val parameters: Parameters = Parameters()
) {

    data class Parameters(
            val type: OpcBasesToBytesUtils.Type = OpcBasesToBytesUtils.Type.MAX
    )

    fun direct(
            byteVector: ByteVector,
            basesMatrix: Matrix<DataOpc2.Base>
    ) {
        basesMatrix.applyEach { i, j, value ->
            OpcBasesToBytesUtils.direct(byteVector, value, parameters.type)
            null
        }
    }

    fun reverse(reader: ByteVector.Read, baseSize: Int, matrixSize: Size): Matrix<DataOpc2.Base> {
        return Matrix.create(matrixSize) {i, j -> OpcBasesToBytesUtils.reverse(reader, baseSize) }
    }
}