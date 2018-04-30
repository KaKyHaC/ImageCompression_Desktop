package ImageCompressionLib.Containers

import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

class DataOpcTest {
    lateinit var dataOpc:DataOpc
    @Before
    fun setUp(){
        dataOpc= DataOpc(Size(10,10))
    }
    @Test
    fun toByteVector() {
        val f=Flag.createDefaultFlag()
        val parameters=Parameters(f, Size(100,100),Size(10,10))
        val vector=ByteVector()

        dataOpc.toByteVector(vector, parameters)
        val res= DataOpc.valueOf(vector,parameters)

        assertEquals(dataOpc,res)
    }

    @Test
    fun equals() {
        assertEquals(dataOpc,dataOpc)
    }

    @Test
    fun copy() {
        val cpy=dataOpc.copy()
        assertEquals(cpy,dataOpc)
    }
}