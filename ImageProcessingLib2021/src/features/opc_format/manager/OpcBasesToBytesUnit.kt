package features.opc_format.manager

import data_model.types.ByteVector
import data_model.types.DataOpc2
import kotlin.experimental.and

class OpcBasesToBytesUnit(
        val type: Type = Type.MAX
) {
    enum class Type { MAX, MIN_AND_MAX }

    fun direct(byteVector: ByteVector, bases: DataOpc2.Base) {
        bases.baseMax.forEach { byteVector.putByte(it.toByte()) }
        if (type == Type.MIN_AND_MAX)
            (bases as DataOpc2.Base.MaxMin).baseMin.forEach { byteVector.putByte(it.toByte()) }
    }


    fun reverse(reader: ByteVector.Read, baseSize: Int): DataOpc2.Base {
        val max = ShortArray(baseSize) { reader.nextByte().toShort() and 0xff }
        return if (type == Type.MIN_AND_MAX) {
            val min = ShortArray(baseSize) { reader.nextByte().toShort() and 0xff }
            DataOpc2.Base.MaxMin(max, min)
        } else {
            DataOpc2.Base.Max(max)
        }
    }
}