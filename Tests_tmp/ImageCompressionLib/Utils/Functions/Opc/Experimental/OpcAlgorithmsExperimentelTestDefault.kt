package ImageCompressionLib.Utils.Functions.Opc.Experimental

import ImageCompressionLib.Data.Matrix.ShortMatrix
import ImageCompressionLib.Data.Type.DataOpc
import ImageCompressionLib.Data.Type.Size
import ImageCompressionLib.Utils.Functions.Opc.OpcUtils
import org.junit.Before

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.math.BigInteger
import java.util.*

@RunWith(Parameterized::class)
class OpcAlgorithmsExperimentelTestDefault(val size:Size,val data:MessageAndPositionArray,val coef:Int) {
    lateinit var dataOriginal:ShortMatrix
    lateinit var dataOpc:DataOpc
    lateinit var BiCoef:BigInteger
    @Before
    fun setUp() {
        dataOriginal= ShortMatrix(size){ i, j ->  Math.abs(Random().nextInt(255)).toShort()}
        dataOpc= DataOpc(size)
        OpcUtils.FindBase(dataOriginal,dataOpc)
        BiCoef=BigInteger.valueOf(coef.toLong())
    }
    @Test
    fun testExperementalOpcAlgorithmsLogined(){
        val cpy=dataOriginal.copy()
        val mesCpy=data.copy()
        OpcAlgorithmsExperimentel.OpcDirectWithMessageAt(dataOriginal,dataOpc,data,BiCoef)
        OpcAlgorithmsExperimentel.OpcReverceWithMessageAt(dataOriginal,dataOpc,data,BiCoef)

        println(cpy)
        println(dataOriginal)

        println(mesCpy)
        println(data)

        assertEquals(mesCpy,data)
        assertEquals(cpy,dataOriginal)

    }
    @Test
    fun testExperementalOpcAlgorithmsNotLogined(){
        val cpy=dataOriginal.copy()
        val mesCpy=data.copy()
        OpcAlgorithmsExperimentel.OpcDirectWithMessageAt(dataOriginal,dataOpc,data,BiCoef)
        OpcAlgorithmsExperimentel.OpcReverceWithMessageAt(dataOriginal,dataOpc,data, BigInteger.ONE)

        println(cpy)
        println(dataOriginal)

        println(mesCpy)
        println(data)

//        assertEquals(mesCpy,data)
        assertEquals(cpy,dataOriginal)

    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0},{1},{2}")
        fun data(): Collection<Array<Any>> {
            val data=MessageAndPositionArray()
            data.addMessageAndPosition(true,0)
            val data2=data.copy()
            data2.addMessageAndPosition(false,1)
            val data3=data.copy()
            data3.addMessageAndPosition(true,2)
            return listOf(
                    arrayOf(Size(8,8),data,1),
//                    arrayOf(Size(8,8),data,4),
                    arrayOf(Size(8,8),data,2),
                    arrayOf(Size(8,8),data2,1),
                    arrayOf(Size(8,8),data2,2),
                    arrayOf(Size(8,8),data3,1),
                    arrayOf(Size(8,8),data3,2),
                    arrayOf(Size(8,8),data3,4)
            )
        }
    }
}