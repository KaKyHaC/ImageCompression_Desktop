package ImageCompressionLib.utils.functions

import org.junit.Test

import org.junit.Assert.*

class ByteArrayConvertorTest {
    val instance=ByteArrayConvertor.instance

    @Test
    fun testInt(){
        toInt(0)
        toInt(1)
        toInt(10)
        toInt(112413)
        toInt(11241123)
        toInt(-112413)
    }
    @Test
    fun testShort(){
        toShort(0)
        toShort(1)
        toShort(124)
        toShort(-124)
    }
    fun toInt(i:Int) {
        val ar=instance.fromInt(i)
        val res=instance.toInt(ar)
        assertEquals(i,res)
    }
    fun toShort(i:Short) {
        val ar=instance.fromShort(i)
        val res=instance.toShort(ar)
        assertEquals(i,res)
    }
}