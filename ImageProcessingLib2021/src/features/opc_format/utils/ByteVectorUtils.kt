package features.opc_format.utils

import data_model.generics.matrix.Matrix
import data_model.types.ByteVector
import data_model.types.DataOpc2
import data_model.types.Size
import java.math.BigInteger
import java.util.*
import kotlin.experimental.and

object ByteVectorUtils {

    object Bases {

        enum class Type { MAX, MIN_AND_MAX }

        fun direct(byteVector: ByteVector, bases: DataOpc2.Base, type: Type = Type.MAX) {
            bases.baseMax.forEach { byteVector.putByte(it.toByte()) }
            if (type == Type.MIN_AND_MAX)
                (bases as DataOpc2.Base.MaxMin).baseMin.forEach { byteVector.putByte(it.toByte()) }
        }

        fun reverse(reader: ByteVector.Read, baseSize: Int, type: Type = Type.MAX): DataOpc2.Base {
            val max = ShortArray(baseSize) { reader.nextByte().toShort() and 0xff }
            return if (type == Type.MIN_AND_MAX) {
                val min = ShortArray(baseSize) { reader.nextByte().toShort() and 0xff }
                DataOpc2.Base.MaxMin(max, min)
            } else {
                DataOpc2.Base.Max(max)
            }
        }

    }

    object Size {
        fun direct(byteVector: ByteVector, size: data_model.types.Size) {
            byteVector.putShort(size.width.toShort())
            byteVector.putShort(size.height.toShort())
        }

        fun reverse(reader: ByteVector.Read) = data_model.types.Size(
                reader.nextShort().toInt(),
                reader.nextShort().toInt()
        )
    }

    object Code {
        fun direct(byteVector: ByteVector, code: DataOpc2.Code.BI, bases: DataOpc2.Base, unitSize: data_model.types.Size) {
            val lengthOfCode = bases.getLengthOfCode(unitSize)
            val toByteArray = code.N.toByteArray()
            val offset = lengthOfCode - toByteArray.size
            val list = List(lengthOfCode) { if (it >= offset) toByteArray[it - offset] else 0b0 }
            byteVector.putArray(list)
        }

        fun reverse(reader: ByteVector.Read, bases: DataOpc2.Base, unitSize: data_model.types.Size): DataOpc2.Code.BI {
            val lengthOfCode = bases.getLengthOfCode(unitSize)
            val nextBytes = reader.nextBytes(lengthOfCode)
            val bigInteger = BigInteger(nextBytes)
            return DataOpc2.Code.BI(bigInteger)
        }
    }

    object Bits {
        fun direct(byteVector: ByteVector, bits: Matrix<Boolean>) {
            val bitSet = BitSet(bits.width * bits.height)
            var counter = 0
            bits.applyEach { i, j, value -> bitSet.set(counter++, value); null }
            byteVector.putArray(bitSet.toByteArray().toList())
        }

        fun reverse(reader: ByteVector.Read, size: data_model.types.Size): Matrix<Boolean> {
            val len = size.width * size.height / 8
            val nextBytes = reader.nextBytes(len)
            val valueOf = BitSet.valueOf(nextBytes)
            var counter = 0
            return Matrix.create(size) { i, j -> valueOf.get(counter++) }
        }
    }
}