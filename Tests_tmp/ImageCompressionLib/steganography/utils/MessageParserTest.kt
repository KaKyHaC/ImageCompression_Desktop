package ImageCompressionLib.steganography.utils

import org.junit.Test

import org.junit.Assert.*
import java.util.*

class MessageParserTest {
    val mp= MessageParser()
    @Test
    fun parseMessage() {
        test(4)
        test(40)
        test(400)
        test(4000)
    }
    fun test(size:Int){
        val b=ByteArray(size){Random().nextInt().toByte()}
        val m=mp.parseMessage(b)
        val res=mp.parseMessage(m)
        assertArrayEquals(b,res)
    }
    @Test
    fun testString(){
        val s=Random().nextLong().toString()
        val m=mp.fromString(s)
        val res=mp.toString(m)
        assertEquals(s,res)
    }
}