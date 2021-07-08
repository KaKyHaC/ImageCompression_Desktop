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
            val writeAC: Boolean = true,
            val writeSign: Boolean = true,
            val unitSize: Size = Size(8)
    )

    fun direct(
            byteVector: ByteVector,
            basesMatrix: Matrix<DataOpc2>
    ) {
        basesMatrix.applyEach { i, j, value ->
            if (parameters.writeAC) byteVector.putShort(value.AC!!)
            if (parameters.writeSign) ByteVectorUtils.Bits.direct(byteVector, value.sign!!)
            ByteVectorUtils.Code.direct(byteVector, value.code as DataOpc2.Code.BI, value.base, parameters.unitSize)
            null
        }
    }

    fun reverse(reader: ByteVector.Read, basesMatrix: Matrix<DataOpc2.Base>): Matrix<DataOpc2> {
        return basesMatrix.map { i, j, value ->
            val builder = DataOpc2.Builder(originSize = parameters.unitSize)
            builder.AC = if (parameters.writeAC) reader.nextShort() else null
            builder.sign = if (parameters.writeSign) ByteVectorUtils.Bits.reverse(reader, parameters.unitSize) else null
            builder.N = ByteVectorUtils.Code.reverse(reader, value, parameters.unitSize).N
            builder.baseMax = value.baseMax
            builder.baseMin = (value as? DataOpc2.Base.MaxMin)?.baseMin
            builder.build()
        }
    }
}