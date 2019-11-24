package ImageCompressionLib.Data

import ImageCompressionLib.Data.Primitives.ByteVector
import ImageCompressionLib.Data.Primitives.DataOpc
import ImageCompressionLib.Data.Type.Flag
import ImageCompressionLib.Data.Primitives.Size
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import java.math.BigInteger
import java.util.*

class DataOpcTest {
    lateinit var dataOpc: DataOpc
    val size= Size(10, 10)
    val rand=Random()
    @Before
    fun setUp(){
        dataOpc= DataOpc(size)
        dataOpc.DC=rand.nextShort(255)
        dataOpc.N= BigInteger.valueOf(rand.nextShort(3000).toLong())
        for(i in 0 until size.width){
            for(j in 0 until size.height){
                dataOpc.base[j]=rand.nextShort(255)
                dataOpc.sign[i][j]=rand.nextBoolean()
            }
        }
    }
    @Test
    fun toByteVector() {
        val f= Flag.createDefaultFlag()
        f.setFalse(Flag.Parameter.LongCode)
        val parameters=Parameters(f, Size(100, 100),size)
        val vector = ByteVector()

//        dataOpc.FromBaseToVector(vector,f)
        dataOpc.toByteVector(vector, parameters)
        val res= DataOpc.valueOf(vector,parameters)

        assertEquals(dataOpc,res)
    }
    @Test
    fun signToByteVector() {
        val f= Flag.createDefaultFlag()
        val vector= ByteVector()
        val cpy=dataOpc.copy()

        dataOpc.FromSignToVector(vector)
        cpy.FromVectorToSign(vector)

        assertEquals(dataOpc,cpy)
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

    fun Random.nextByte():Byte{
        var i=nextInt() and 0xff
        if(i<0)
           i*=-1
        return i.toByte()
    }
    fun Random.nextShort(rad:Int): Short {
        var res=nextInt().toShort()%rad
        if(res<0)
            res*=-1
        return res.toShort()
    }
}