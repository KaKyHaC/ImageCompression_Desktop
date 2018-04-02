package ImageCompression.ProcessingModules

import org.junit.Before

import org.junit.Assert.*
import org.junit.Test

class StegoEncrWithOPCTest {
    @Before
    fun setUp() {
    }

    @Test
    fun TestByteArrayToString(){
        val s="212dssd223"
        val ba=s.toByteArray()
        val res= String(ba)
        assertEquals(s,res)
    }
}