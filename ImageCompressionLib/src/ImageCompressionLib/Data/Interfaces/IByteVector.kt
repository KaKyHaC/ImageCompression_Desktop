package ImageCompressionLib.Data.Interfaces

interface IByteVector : Iterable<Byte> {
    val bytes: ByteArray
    val size get() = bytes.size

    fun append(value: Boolean)
    fun append(value: Byte)
    fun append(value: Short)
    fun append(value: Int)
    fun append(value: Long)

    fun getNextBoolean(index: Int? = null): Boolean?
    fun getNextByte(index: Int? = null): Byte?
    fun getNextShort(index: Int? = null): Short?
    fun getNextInt(index: Int? = null): Int?
    fun getNextLong(index: Int? = null): Long?
}