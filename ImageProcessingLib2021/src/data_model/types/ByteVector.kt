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
        shortBuffer.putShort(value)
        shortBuffer.array().forEach { vector.add(it) }
        shortBuffer.position(0)
    }

    fun putInt(value: Int) {
        intBuffer.putInt(value)
        intBuffer.array().forEach { vector.add(it) }
        intBuffer.position(0)
    }

    fun putLong(value: Long) {
        longBuffer.putLong(value)
        longBuffer.array().forEach { vector.add(it) }
        longBuffer.position(0)
    }

    fun getReader() = Read(vector.toByteArray())

    fun getBytes() = vector.toByteArray()

    override fun toString() = vector.toString()

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
