package ImageCompression.Steganography.Utils

import java.io.*
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
    fun fromString(message:String):BooleanArray{
        val bytes=message.toByteArray()
        return parseMessage(bytes)
    }
    fun toString(mesage:BooleanArray):String{
        val bytes=parseMessage(mesage)
        return String(bytes)
    }
    fun toFile(message:BooleanArray,file:File){
        if(!file.exists())
            file.createNewFile()

        val os = java.io.DataOutputStream(FileOutputStream(file))
        val bytes=parseMessage(message)
        val len=bytes.size
        os.write(bytes)
        os.close()
    }
    fun fromFile(file: File):BooleanArray{
        val inputStream=DataInputStream(FileInputStream(file))
        val bytes=inputStream.readAllBytes()
        inputStream.close()
        val res=parseMessage(bytes)
        return res
    }
}