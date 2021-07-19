package features.opc_format.manager

import data_model.generics.Triple
import data_model.generics.matrix.Matrix
import data_model.types.ByteVector
import data_model.types.DataOpc2
import data_model.types.Size
import features.opc_format.utils.ByteVectorUtils
import kotlin.experimental.and

class SizeToBytesManager(
        val parameters: Parameters = Parameters()
) {

    data class Parameters(
            val sameSize: Boolean = false
    )

    fun direct(
            byteVector: ByteVector = ByteVector(),
            triple: Triple<Size>
    ) {
        if (parameters.sameSize) {
            ByteVectorUtils.Size.direct(byteVector, triple.first)
        } else {
            ByteVectorUtils.Size.direct(byteVector, triple.first)
            ByteVectorUtils.Size.direct(byteVector, triple.second)
            ByteVectorUtils.Size.direct(byteVector, triple.third)
        }
    }

    fun reverse(reader: ByteVector.Read): Triple<Size> {
        return if (parameters.sameSize) {
            val tmp = ByteVectorUtils.Size.reverse(reader)
            Triple(tmp, tmp, tmp)
        } else {
            val a = ByteVectorUtils.Size.reverse(reader)
            val b = ByteVectorUtils.Size.reverse(reader)
            val c = ByteVectorUtils.Size.reverse(reader)
            Triple(a, b, c)
        }
    }
}