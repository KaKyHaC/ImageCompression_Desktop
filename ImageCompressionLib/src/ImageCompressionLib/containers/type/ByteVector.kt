package ImageCompressionLib.containers.type

import ImageCompressionLib.constants.BITS_PER_BYTE
import java.util.*
import kotlin.collections.ArrayList

/**
 * Non save class
 * Iterator for Array<Byte>
 */
class ByteVector:Iterable<Byte> {
    var bytes: ByteArray
    var size: Int
        private set;
    var maxSize: Int
        private set;
    var curIndex: Int
        private set;

    constructor(size: Int = 10) {
        maxSize = size
        if (size <= 0)
            maxSize = 10

        val zero: Byte = 0
        bytes = ByteArray(maxSize);
        this.size = 0
        curIndex = 0
    }

    constructor(byteArray: ByteArray) {
        maxSize = byteArray.size
        bytes=byteArray;
        this.size = byteArray.size
        curIndex = 0
    }
    private constructor(byteArray: Array<Byte>){
        bytes= ByteArray(byteArray.size){ i->byteArray[i]}
        this.maxSize=byteArray.size
        this.size=byteArray.size
        curIndex=0
    }

    fun toByteArray(): ByteArray {
        return ByteArray(size){i->bytes[i]}
    }

    fun append(byte: Byte) {
        if (size >= maxSize - 1) {
            grow(maxSize * 2)
        }
        bytes[size++] = byte
    }

    fun append(short: Short) {
        append((short.toInt() shr BITS_PER_BYTE).toByte())
        append(short.toByte())
    }

    fun append(long: Long) {
        //todo replace with tmp
        append((long shr (BITS_PER_BYTE * 7)).toByte())
        append((long shr (BITS_PER_BYTE * 6)).toByte())
        append((long shr (BITS_PER_BYTE * 5)).toByte())
        append((long shr (BITS_PER_BYTE * 4)).toByte())
        append((long shr (BITS_PER_BYTE * 3)).toByte())
        append((long shr (BITS_PER_BYTE * 2)).toByte())
        append((long shr BITS_PER_BYTE).toByte())
        append(long.toByte())
    }

    operator fun get(index: Int): Byte {
        return bytes[index]
    }

    fun getByteAt(index: Int): Byte {
        return bytes[index]
    }

    fun getNext(): Byte {
        return bytes[curIndex++]
    }

    fun getShortFrom(index: Int): Short {
        var res = bytes[index].toInt() and 0xFF
        res = (res shl BITS_PER_BYTE)
        res = res or (bytes[index + 1].toInt() and 0xFF)
        return res.toShort()
    }

    fun getNextShort(): Short {
        var res = bytes[curIndex++].toInt() and 0xFF
        res = (res shl BITS_PER_BYTE)
        res = res or (bytes[curIndex++].toInt() and 0xFF)
        return res.toShort()
    }

    fun getLongFrom(index: Int): Long {
        var res = (bytes[index].toLong() and 0xFF)
        res = (res shl BITS_PER_BYTE)
        res = res or (bytes[index + 1].toLong() and 0xFF)
        res = (res shl BITS_PER_BYTE)
        res = res or (bytes[index + 2].toLong() and 0xFF)
        res = (res shl BITS_PER_BYTE)
        res = res or (bytes[index + 3].toLong() and 0xFF)
        res = (res shl BITS_PER_BYTE)
        res = res or (bytes[index + 4].toLong() and 0xFF)
        res = (res shl BITS_PER_BYTE)
        res = res or (bytes[index + 5].toLong() and 0xFF)
        res = (res shl BITS_PER_BYTE)
        res = res or (bytes[index + 6].toLong() and 0xFF)
        res = (res shl BITS_PER_BYTE)
        res = res or (bytes[index + 7].toLong() and 0xFF)
        return res
    }

    fun getNextLong(): Long {
        var res = (bytes[curIndex++].toLong() and 0xFF)
        res = (res shl BITS_PER_BYTE)
        res = res or (bytes[curIndex++].toLong() and 0xFF)
        res = (res shl BITS_PER_BYTE)
        res = res or (bytes[curIndex++].toLong() and 0xFF)
        res = (res shl BITS_PER_BYTE)
        res = res or (bytes[curIndex++].toLong() and 0xFF)
        res = (res shl BITS_PER_BYTE)
        res = res or (bytes[curIndex++].toLong() and 0xFF)
        res = (res shl BITS_PER_BYTE)
        res = res or (bytes[curIndex++].toLong() and 0xFF)
        res = (res shl BITS_PER_BYTE)
        res = res or (bytes[curIndex++].toLong() and 0xFF)
        res = (res shl BITS_PER_BYTE)
        res = res or (bytes[curIndex++].toLong() and 0xFF)
        return res
    }

    private var bitSetCounter = BITS_PER_BYTE
    private var boolByteSetIndex = 0
    fun append(boolean: Boolean) {
        if (bitSetCounter == BITS_PER_BYTE) {
            bitSetCounter = 0
            boolByteSetIndex = size++
        }
        if (size >= maxSize - 1)
            grow(maxSize * 2)

        val v = if (boolean) Math.pow(2.0, bitSetCounter.toDouble()).toInt() else 0
        var tmp = bytes[boolByteSetIndex].toInt()// and 0xff
        tmp = tmp or v
        bytes[boolByteSetIndex] = tmp.toByte()
        bitSetCounter++
    }

    private var bitGetCounter = BITS_PER_BYTE
    private var boolByteGetIndex = 0
    fun getNextBoolean(): Boolean {
        if (bitGetCounter == BITS_PER_BYTE) {
            bitGetCounter = 0
            boolByteGetIndex = curIndex++
        }

        val bitN = Math.pow(2.0, bitGetCounter.toDouble()).toInt()
        val res = bytes[boolByteGetIndex].toInt() and bitN == bitN
        bitGetCounter++
        return res
    }

    fun refreshIterator() {
        curIndex = 0
    }

    private fun grow(newSize: Int) {
        bytes = ByteArray(newSize, { x ->
            if (x < size)
                bytes[x]
            else
                0
        })
        maxSize = newSize
    }

    fun forEach(myAction: ((byte: Byte) -> Unit)) {
        for (i in 0..size - 1)
            myAction.invoke(bytes[i])
    }

    fun copy(): ByteVector {
        val res = ByteVector(size+1)
        this.forEach{ byte -> res.append(byte) }
        return res
    }

    override fun iterator(): Iterator<Byte> {
        class MyIterator : Iterator<Byte> {
            var cur = 0
            override fun hasNext(): Boolean {
                return cur < size
            }

            override fun next(): Byte {
                return bytes[cur++]
            }
        }
        return MyIterator()
    }

    fun hasNext(): Boolean {
        return curIndex < size
    }

    fun hasNextBit(): Boolean {
//        var tmp=boolByteGetIndex
//        if(bitGetCounter == BITS_PER_BYTE)
//            tmp++
        if (bitGetCounter == BITS_PER_BYTE) {
            bitGetCounter = 0
            boolByteGetIndex = curIndex++
        }
        return boolByteGetIndex < size
    }

    operator fun plus(vector: ByteVector): ByteVector {
        val res=ByteVector(size+vector.size+1)
        for (i in 0 until size)
            res.append(bytes[i])
        for (i in 0 until vector.size)
            res.append(vector.bytes[i])

        return res
    }

    /**
     * remove last zero elements
     * @return this object
     */
    fun trim():ByteVector{
        for(i in size-1 downTo 0) {
            if (bytes[i].toInt() == 0)
                size--
            else
                return this
        }
        return this
    }
    fun split(len:Int):List<ByteVector>{
        val res=ArrayList<ByteVector>()
        val i=0
        while(i<size) {
            val tmp = ByteVector()
            for (j in 0 until len)
                tmp.append(bytes[i])
            res.add(tmp)
        }
        return res
    }
    operator fun div(times:Int):Array<ByteVector>{
        val res=Array<ByteVector>(times){ ByteVector() }
        val tmpLen=size/times+if(size%times!=0)1 else 0
        var i=0
        for(count in 0 until times){
            val tmp=ByteVector()
            for(j in 0 until tmpLen)
                tmp.append(if(i<size)bytes[i++] else 0b0)
            res[count]=tmp
        }
        return res
    }

    override fun equals(other: Any?): Boolean {
        if (other is ByteVector) {
            if (this.size != other.size)
                return false
            for (i in 0..size - 1) {
                if (this[i] != other[i])
                    return false
            }
            return true
        } else
            return false
    }

    fun assertEquals(other: ByteVector): Boolean {
        val size = if (this.size < other.size) this.size else other.size
        for (i in 0 until size) {
            if (other.bytes[i] != bytes[i])
                throw Exception("this:$this \nother:$other")
        }
        return true
    }

    override fun hashCode(): Int {
        var result = Arrays.hashCode(bytes)
        result = 31 * result + size
        result = 31 * result + maxSize
        result = 31 * result + curIndex
        return result
    }

    override fun toString(): String {
        return Arrays.toString(bytes)
    }
}
