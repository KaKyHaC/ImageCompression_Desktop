package features.opc_format.manager

import data_model.generics.matrix.Matrix
import data_model.types.ByteVector
import data_model.types.DataOpc2
import data_model.types.Size
import features.opc_format.utils.ByteVectorUtils
import kotlin.experimental.and

class OpcBasesToBytesManager(
        val parameters: Parameters = Parameters()
) {

    data class Parameters(
            val sameBaseLength: Boolean = true,
            val sameMatrixSize: Boolean = true,
            val firth: OpcBasesToBytesUnit.Parameters = OpcBasesToBytesUnit.Parameters(),
            val second: OpcBasesToBytesUnit.Parameters = OpcBasesToBytesUnit.Parameters(),
            val third: OpcBasesToBytesUnit.Parameters = OpcBasesToBytesUnit.Parameters()
    )

    private val unit1 = OpcBasesToBytesUnit(parameters.firth)
    private val unit2 = OpcBasesToBytesUnit(parameters.second)
    private val unit3 = OpcBasesToBytesUnit(parameters.third)

    fun direct(
            byteVector: ByteVector = ByteVector(),
            triple: data_model.generics.Triple<Matrix<DataOpc2.Base>>
    ) {
        if (parameters.sameMatrixSize) {
            ByteVectorUtils.Size.direct(byteVector, triple.first.size)
        } else {
            ByteVectorUtils.Size.direct(byteVector, triple.first.size)
            ByteVectorUtils.Size.direct(byteVector, triple.second.size)
            ByteVectorUtils.Size.direct(byteVector, triple.third.size)
        }

        if (parameters.sameBaseLength) {
            byteVector.putByte(triple.first[0, 0].baseMax.size.toByte())
        } else {
            byteVector.putByte(triple.second[0, 0].baseMax.size.toByte())
            byteVector.putByte(triple.second[0, 0].baseMax.size.toByte())
            byteVector.putByte(triple.third[0, 0].baseMax.size.toByte())
        }

        unit1.direct(byteVector, triple.first)
        unit2.direct(byteVector, triple.second)
        unit3.direct(byteVector, triple.third)
    }

    fun reverse(reader: ByteVector.Read): data_model.generics.Triple<Matrix<DataOpc2.Base>> {
        val (size1, size2, size3) = if (parameters.sameMatrixSize) {
            val tmp = ByteVectorUtils.Size.reverse(reader)
            Triple(tmp, tmp, tmp)
        } else {
            val tmp1 = ByteVectorUtils.Size.reverse(reader)
            val tmp2 = ByteVectorUtils.Size.reverse(reader)
            val tmp3 = ByteVectorUtils.Size.reverse(reader)
            Triple(tmp1, tmp2, tmp3)
        }

        val (len1, len2, len3) = if (parameters.sameBaseLength) {
            val tmp = reader.nextByte().toInt()
            Triple(tmp, tmp, tmp)
        } else {
            val tmp1 = reader.nextByte().toInt()
            val tmp2 = reader.nextByte().toInt()
            val tmp3 = reader.nextByte().toInt()
            Triple(tmp1, tmp2, tmp3)
        }

        val matrix1 = unit1.reverse(reader, len1, size1)
        val matrix2 = unit1.reverse(reader, len2, size2)
        val matrix3 = unit1.reverse(reader, len3, size3)

        return data_model.generics.Triple(matrix1, matrix2, matrix3)
    }
}