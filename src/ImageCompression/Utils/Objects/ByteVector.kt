package ImageCompression.Utils.Objects

import java.util.*
import java.util.function.Consumer
import kotlin.experimental.and
import kotlin.experimental.or

/**
 * Non save class
 * Iterator for Array<Byte>
 */
class ByteVector(size:Int=10):Iterable<Byte> {
    companion object {
        @JvmStatic val BITS_IN_BYTE=8
    }
    var bytes:Array<Byte>
    var size:Int
        private set;
    var maxSize:Int
        private set;
    var curIndex:Int
        private set;

    init {
        maxSize=size
        if(size<=0)
            maxSize=10

        val zero:Byte = 0
        bytes= Array(maxSize,{ x->zero})
        this.size=0
        curIndex=0
    }

    fun getByteArray():ByteArray{
        return ByteArray(size,{x->bytes[x]})
    }

    fun append(byte: Byte){
        if(size==maxSize-1){
            grow(maxSize*2)
        }
        bytes[size++]=byte
    }
    fun append(short:Short){
        append((short.toInt() shr BITS_IN_BYTE).toByte())
        append(short.toByte())
    }
    fun append(long: Long){
        append((long shr (BITS_IN_BYTE*7)).toByte())
        append((long shr (BITS_IN_BYTE*6)).toByte())
        append((long shr (BITS_IN_BYTE*5)).toByte())
        append((long shr (BITS_IN_BYTE*4)).toByte())
        append((long shr (BITS_IN_BYTE*3)).toByte())
        append((long shr (BITS_IN_BYTE*2)).toByte())
        append((long shr BITS_IN_BYTE).toByte())
        append(long.toByte())
    }

    operator fun get(index: Int):Byte{
        return bytes[index]
    }
    fun getByteAt(index:Int):Byte{
        return bytes[index]
    }
    fun getNext():Byte{
        return bytes[curIndex++]
    }

    fun getShortFrom(index: Int):Short{
        var res=bytes[index].toInt()and 0xFF
        res=(res shl BITS_IN_BYTE)
        res = res or (bytes[index+1].toInt()and 0xFF)
        return res.toShort()
    }
    fun getNextShort():Short{
        var res=bytes[curIndex++].toInt()and 0xFF
        res=(res shl BITS_IN_BYTE)
        res = res or (bytes[curIndex++].toInt()and 0xFF)
        return res.toShort()
    }

    fun getLongFrom(index: Int):Long{
        var res=(bytes[index].toLong() and 0xFF)
        res=(res shl BITS_IN_BYTE)
        res = res or (bytes[index+1].toLong()and 0xFF)
        res=(res shl BITS_IN_BYTE)
        res = res or (bytes[index+2].toLong()and 0xFF)
        res=(res shl BITS_IN_BYTE)
        res = res or (bytes[index+3].toLong()and 0xFF)
        res=(res shl BITS_IN_BYTE)
        res = res or (bytes[index+4].toLong()and 0xFF)
        res=(res shl BITS_IN_BYTE)
        res = res or (bytes[index+5].toLong()and 0xFF)
        res=(res shl BITS_IN_BYTE)
        res = res or (bytes[index+6].toLong()and 0xFF)
        res=(res shl BITS_IN_BYTE)
        res = res or (bytes[index+7].toLong()and 0xFF)
        return res
    }
    fun getNextLong():Long{
        var res=(bytes[curIndex++].toLong() and 0xFF)
        res=(res shl BITS_IN_BYTE)
        res = res or (bytes[curIndex++].toLong()and 0xFF)
        res=(res shl BITS_IN_BYTE)
        res = res or (bytes[curIndex++].toLong()and 0xFF)
        res=(res shl BITS_IN_BYTE)
        res = res or (bytes[curIndex++].toLong()and 0xFF)
        res=(res shl BITS_IN_BYTE)
        res = res or (bytes[curIndex++].toLong()and 0xFF)
        res=(res shl BITS_IN_BYTE)
        res = res or (bytes[curIndex++].toLong()and 0xFF)
        res=(res shl BITS_IN_BYTE)
        res = res or (bytes[curIndex++].toLong()and 0xFF)
        res=(res shl BITS_IN_BYTE)
        res = res or (bytes[curIndex++].toLong()and 0xFF)
        return res
    }

    fun refreshIterator(){
        curIndex=0
    }

    private fun grow(newSize:Int){
        bytes=Array(newSize,{x->
            if(x<size)
                bytes[x]
            else
                0
        })
        maxSize=newSize
    }

    fun forEach(myAction:((byte:Byte)->Unit) ) {
        for(i in 0..size-1)
            myAction.invoke(bytes[i])
    }
    fun copy():ByteVector{
        val res=ByteVector(size)
        this.forEach(myAction= {byte->res.append(byte)})
        return res
    }

    override fun iterator(): Iterator<Byte> {
        class MyIterator:Iterator<Byte>{
            val cur=0
            override fun hasNext(): Boolean {
                return cur<size
            }

            override fun next(): Byte {
                return bytes[cur]
            }
        }
        return MyIterator()
    }
    override fun equals(other: Any?): Boolean {
        if(other is ByteVector){
            if(this.size!=other.size)
                return false
            for(i in 0..size-1){
                if(this[i]!=other[i])
                    return false
            }
            return true
        }else
            return false
    }
}