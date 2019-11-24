package ImageCompressionLib.Data

import ImageCompressionLib.Data.Type.ByteVector
import ImageCompressionLib.Data.Type.Size
import org.junit.Before

import org.junit.Assert.*
import org.junit.Test
import java.io.File
import java.util.*

class ByteVectorContainerTest {
    lateinit var bvc:ByteVectorContainer
    val file= File("fileTest.txt")
    @Before
    fun setUp() {
        val mainD= ByteVector(8000)
        for(i in 0 until mainD.maxSize)
            mainD.append(Random().nextLong())

        bvc= ByteVectorContainer(Parameters.createParametresForTest(Size(100,100)),mainD,mainD)
        file.createNewFile()
    }

    @Test
    fun bvcIO(){
        bvc.writeToStream(file.outputStream())
        val res=ByteVectorContainer.readFromStream(file.inputStream())
        assertEquals(bvc,res)
    }
    @Test
    fun testEquals(){
        assertEquals(bvc,bvc)
    }
}