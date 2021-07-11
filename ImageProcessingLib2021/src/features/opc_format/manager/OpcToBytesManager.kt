package features.opc_format.manager

import data_model.generics.Triple
import data_model.generics.matrix.Matrix
import data_model.types.ByteVector
import data_model.types.DataOpc2
import data_model.types.Size
import features.opc_format.utils.ByteVectorUtils
import kotlin.experimental.and

class OpcToBytesManager(
        val parameters: Parameters = Parameters()
) {

    data class Parameters(
            val firth: OpcToBytesUnit.Parameters = OpcToBytesUnit.Parameters(),
            val second: OpcToBytesUnit.Parameters = OpcToBytesUnit.Parameters(),
            val third: OpcToBytesUnit.Parameters = OpcToBytesUnit.Parameters()
    )

    private val unit1 = OpcToBytesUnit(parameters.firth)
    private val unit2 = OpcToBytesUnit(parameters.second)
    private val unit3 = OpcToBytesUnit(parameters.third)

    fun direct(
            byteVector: ByteVector = ByteVector(),
            triple: data_model.generics.Triple<Matrix<DataOpc2>>
    ) {
        unit1.direct(byteVector, triple.first)
        unit2.direct(byteVector, triple.second)
        unit3.direct(byteVector, triple.third)
    }

    fun reverse(reader: ByteVector.Read, triple: Triple<Matrix<DataOpc2.Base>>): data_model.generics.Triple<Matrix<DataOpc2>> {
        val matrix1 = unit1.reverse(reader, triple.first)
        val matrix2 = unit1.reverse(reader, triple.second)
        val matrix3 = unit1.reverse(reader, triple.third)

        return data_model.generics.Triple(matrix1, matrix2, matrix3)
    }
}