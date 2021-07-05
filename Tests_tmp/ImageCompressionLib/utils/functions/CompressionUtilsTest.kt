package ImageCompressionLib.utils.functions

import org.junit.Assert.*
import org.junit.Test
import java.util.*

class CompressionUtilsTest{

    @Test
    fun TestAssert2(){
        testCompressionUtils(10000,2)
    }
    @Test
    fun TestAssert10(){
        testCompressionUtils(10000,10)
    }
    @Test
    fun TestAssert255(){
        testCompressionUtils(10000,100)
    }

    fun testCompressionUtils(size: Int,bound: Int){
        val ba=createByteArray(size,bound)
        val cba=CompressionUtils.compress(ba)
        assertTrue(cba.size<ba.size)

        val rba=CompressionUtils.decompress(cba)
        assertArrayEquals(rba,ba)
    }
    fun createByteArray(size:Int,bound:Int=10000):ByteArray{
        val rand=Random()
        return kotlin.ByteArray(size,{x->rand.nextInt(bound).toByte()})
    }
}