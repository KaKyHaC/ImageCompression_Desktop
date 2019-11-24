package ImageCompressionLib.Data.Type

import ImageCompressionLib.Data.Interfaces.IByteVector
import java.nio.ByteBuffer
import kotlin.experimental.or

/**
 * Non save class
 * Iterator for Array<Byte>
 */
class ByteVector : IByteVector{

    private val byteBuffer = ByteBuffer.allocate(64)

    private val data = mutableListOf<Byte>()

    var byteIndex = 0
        private set

    var bitIndex = 0
        private set

    override val bytes: ByteArray
        get() = data.toByteArray()

    fun resetIterator() {
        bitIndex = 0
        byteIndex = 0
    }

    override fun append(value: Boolean) {
        val byteValue = if (value) Math.pow(2.0, byteIndex.toDouble()).toByte() else 0
        val byteIndex = if (bitIndex == 0) byteIndex++ else byteIndex - 1
        val currentByte = data.getOrNull(byteIndex) ?: 0
        val newByte = currentByte or byteValue
        data.set(byteIndex, newByte)
    }

    override fun append(value: Byte) {
        data.add(value)
    }

    override fun append(value: Short) {
        bitIndex = 0
        data.addAll(byteBuffer.putShort(value).array().toList())
    }

    override fun append(value: Int) {
        bitIndex = 0
        data.addAll(byteBuffer.putInt(value).array().toList())
    }

    override fun append(value: Long) {
        bitIndex = 0
        data.addAll(byteBuffer.putLong(value).array().toList())
    }

    override fun getNextBoolean(index: Int?): Boolean? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getNextByte(index: Int?) = data.getOrNull(index ?: byteIndex++)

    override fun getNextShort(index: Int?): Short? {
        byteBuffer.put(bytes, index ?: byteIndex++, 2)
        byteBuffer.flip()//need flip
        return byteBuffer.short
    }

    override fun getNextInt(index: Int?): Int? {
        byteBuffer.put(bytes, index ?: byteIndex++, 4)
        byteBuffer.flip()//need flip
        return byteBuffer.int
    }

    override fun getNextLong(index: Int?): Long? {
        byteBuffer.put(bytes, index ?: byteIndex++, 8)
        byteBuffer.flip()//need flip
        return byteBuffer.long
    }

    override fun iterator() = data.iterator()
}
