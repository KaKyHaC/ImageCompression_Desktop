package data_model.types

import java.nio.ByteBuffer
import java.util.*
import kotlin.collections.ArrayList

/**
 * Non save class
 * Iterator for Array<Byte>
 */
class ByteVector {

    val vector = Vector<Byte>()

    fun putByte(byte: Byte) {
        vector.add(byte)
    }

    fun putShort(value: Short) {
        shortBuffer.reset()
        shortBuffer.putShort(value)
        shortBuffer.array().forEach { vector.add(it) }
    }

    fun putInt(value: Int) {
        intBuffer.reset()
        intBuffer.putInt(value)
        intBuffer.array().forEach { vector.add(it) }
    }

    fun putLong(value: Int) {
        longBuffer.reset()
        longBuffer.putInt(value)
        longBuffer.array().forEach { vector.add(it) }
    }

    fun getReader() = Read(vector.toByteArray())

    class Read(byteArray: ByteArray) {
        private val buffer = ByteBuffer.wrap(byteArray)

        fun getByte() = buffer.get()

        fun getShort() = buffer.short

        fun getInt() = buffer.int

        fun getLong() = buffer.long
    }

    companion object {
        private val shortBuffer = ByteBuffer.allocate(java.lang.Short.BYTES)
        private val intBuffer = ByteBuffer.allocate(java.lang.Integer.BYTES)
        private val longBuffer = ByteBuffer.allocate(java.lang.Long.BYTES)
    }
}
