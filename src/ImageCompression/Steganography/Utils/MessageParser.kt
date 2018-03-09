package ImageCompression.Steganography.Utils

import kotlin.experimental.and
import kotlin.experimental.or

class MessageParser {
    val BIT_PER_BYTE=8
    fun parseMessage(m:ByteArray):BooleanArray{
        val res = BooleanArray(m.size*BIT_PER_BYTE)
        var index=0
        for(e in m){
            var byte: Byte = 0b1
            for(i in 0 until BIT_PER_BYTE) {
                res[index++] = (e and byte) == byte
                byte=(byte.toInt() shl 1).toByte()
            }
        }
        return res
    }
    fun parseMessage(m:BooleanArray):ByteArray{
        val res = ByteArray(m.size/BIT_PER_BYTE)
        var index=0
        for((num,e) in res.withIndex()){
            var byte: Byte = 0b1
            for(i in 0 until BIT_PER_BYTE) {
                var buf=res[num]
                if(m[index++])
                    buf= buf or byte
                res[num]=buf
                byte=(byte.toInt() shl 1).toByte()
            }
        }
        return res
    }
}