package ImageCompressionLib.utils.functions

import ImageCompressionLib.constants.BITS_PER_BYTE
import java.math.BigInteger

class ByteArrayConvertor {
    companion object {
        @JvmStatic val instance=ByteArrayConvertor()
    }
    fun fromInt(value:Int):ByteArray{
        val res=ByteArray(4)
        var tmp=value
        res[0]=tmp.toByte()
        tmp=tmp shr BITS_PER_BYTE
        res[1]=tmp.toByte()
        tmp=tmp shr BITS_PER_BYTE
        res[2]=tmp.toByte()
        tmp=tmp shr BITS_PER_BYTE
        res[3]=tmp.toByte()
        return res
    }
    fun toInt(array: ByteArray):Int{
        var res=array[3].toInt() and 0xff
        res = res shl BITS_PER_BYTE
        res = res or (array[2].toInt() and 0xff)
        res = res shl BITS_PER_BYTE
        res = res or (array[1].toInt() and 0xff)
        res = res shl BITS_PER_BYTE
        res = res or (array[0].toInt() and 0xff)
        return res
    }

    fun fromShort(value:Short):ByteArray{
        val res=ByteArray(2)
        var tmp=value.toInt()
        res[0]=tmp.toByte()
        tmp=tmp shr BITS_PER_BYTE
        res[1]=tmp.toByte()
        tmp=tmp shr BITS_PER_BYTE
        return res
    }
    fun toShort(array: ByteArray):Short{
        var res=array[1].toInt() and 0xff
        res = res shl BITS_PER_BYTE
        res = res or (array[0].toInt() and 0xff)
        return res.toShort()
    }

    fun fromBigInt(value: BigInteger):ByteArray{
        return value.toByteArray()
    }
    fun toBigInt(array: ByteArray):BigInteger{
        return BigInteger(array)
    }
}