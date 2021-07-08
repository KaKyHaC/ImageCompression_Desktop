package features.opc_format.manager

import data_model.generics.matrix.Matrix
import data_model.types.ByteVector
import data_model.types.DataOpc2
import data_model.types.Size
import features.opc_format.utils.ByteVectorUtils

class OpcToBytesUnit(
        val parameters: Parameters = Parameters()
) {

    data class Parameters(
            val type: ByteVectorUtils.Bases.Type = ByteVectorUtils.Bases.Type.MAX
    )

    fun direct(
            byteVector: ByteVector,
            basesMatrix: Matrix<DataOpc2>
    ) {
        basesMatrix.applyEach { i, j, value ->
            TODO()
            null
        }
    }

    fun reverse(reader: ByteVector.Read, basesMatrix: Matrix<DataOpc2.Base>): Matrix<DataOpc2> {
        return basesMatrix.map { i, j, value ->
            TODO()
        }
    }
}