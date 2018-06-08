package ImageCompressionLib.Utils.Functions.Opc

import ImageCompressionLib.Containers.Matrix.ShortMatrix
import ImageCompressionLib.Containers.Type.DataOpc
import ImageCompressionLib.Containers.Type.Size
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.math.BigInteger

@RunWith(Parameterized::class)
class OpcAlgorithmsArrayAndBase(val array:Array<Int>,val base:Array<Int>) {

    lateinit var matrix:ShortMatrix
    lateinit var dataOpc:DataOpc
    lateinit var cpy:ShortMatrix
    @Before
    fun setUp() {
        matrix= ShortMatrix(1,array.size){ i, j -> array[j].toShort() }
        dataOpc= DataOpc(Size(1,array.size))
        for(i in 0 until array.size){
            dataOpc.base[i]=base[i].toShort()
        }
        cpy=matrix.copy()
    }

    @Test
    fun testSpesialArrayAndBaseWhenNDiv2() {
        println("not work")
        println(dataOpc)
        OpcAlgorithms.OpcDirectDefault(matrix, dataOpc)
        dataOpc.N/= BigInteger.TWO
        OpcAlgorithms.OpcReverseDefault(matrix, dataOpc)

        println(cpy)
        println(matrix)
        Assert.assertEquals(cpy,matrix)
    }
    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun data(): Collection<Array<Array<Int>>> {
            return listOf(
//                    arrayOf(arrayOf(122,70,124,178,210,188,48,19),arrayOf(123,71,125,179,211,189,49,20)),

//                    arrayOf(arrayOf(122,70,124,178,210,188,48,19),arrayOf(143,71,125,179,211,189,49,20)),
//                    arrayOf(arrayOf(122,70,124,178,210,188,48,19),arrayOf(144,71,125,179,211,189,49,20)),
//                    arrayOf(arrayOf(113,70,124,178,210,188,48,19),arrayOf(144,71,125,179,211,189,49,20)),
//                    arrayOf(arrayOf(113,70,124,178,210,188,48,19),arrayOf(143,71,125,179,211,189,49,20)),
//                    arrayOf(arrayOf(113,71,124,178,210,188,48,19),arrayOf(143,71,125,179,211,189,49,20)),
//                    arrayOf(arrayOf(113,12,124,178,210,188,48,19),arrayOf(143,71,125,179,211,189,49,20)),
//                    arrayOf(arrayOf(111,12,124,179,210,188,48,19),arrayOf(143,71,125,179,211,189,49,20)),
//                    arrayOf(arrayOf(113,12,123,178,210,188,48,19),arrayOf(143,71,125,179,211,189,49,20)),
//                    arrayOf(arrayOf(113,12,122,178,210,188,48,19),arrayOf(143,71,125,179,211,189,49,20)),
//                    arrayOf(arrayOf(113,11,122,178,210,188,48,18),arrayOf(143,71,125,179,211,189,49,20)),
//                    arrayOf(arrayOf(114,12,122,178,210,188,48,18),arrayOf(143,71,125,179,211,189,49,20)),
//                    arrayOf(arrayOf(111,12,122,178,210,188,48,18),arrayOf(143,71,125,179,211,189,49,20)),
//                    arrayOf(arrayOf(111,12,122,178,210,188,48,18),arrayOf(143,71,125,179,211,189,49,20)),
//                    arrayOf(arrayOf(111,12,122,178,210,188,48,18),arrayOf(144,71,125,179,211,189,49,20)),
//                    arrayOf(arrayOf(111,12,122,178,210,188,48,18),arrayOf(143,72,126,178,212,190,50,21)),
//                    arrayOf(arrayOf(111,12,122,178,210,188,48,18),arrayOf(144,72,126,178,212,190,50,21)),
//                    arrayOf(arrayOf(111,12,122,178,210,188,48,19),arrayOf(143,71,125,179,211,189,49,20)),
//                    arrayOf(arrayOf(111,12,122,178,210,188,48,19),arrayOf(144,71,125,179,211,189,49,20)),
//                    arrayOf(arrayOf(111,12,122,178,210,188,48,19),arrayOf(143,72,126,178,212,190,50,21)),
//                    arrayOf(arrayOf(111,12,122,178,210,188,48,19),arrayOf(144,72,126,178,212,190,50,21)),

                    arrayOf(arrayOf(112,11,19),arrayOf(144,72,22)),

                    arrayOf(arrayOf(113,11,19),arrayOf(144,72,22)),//
                    arrayOf(arrayOf(113,12,19),arrayOf(144,72,22)),//
                    arrayOf(arrayOf(113,11,18),arrayOf(144,72,22)),//
                    arrayOf(arrayOf(113,12,18),arrayOf(144,72,22)),//
                    arrayOf(arrayOf(113,11,19),arrayOf(145,72,22)),//
                    arrayOf(arrayOf(113,12,19),arrayOf(145,72,22)),//
                    arrayOf(arrayOf(113,11,18),arrayOf(145,72,22)),//
                    arrayOf(arrayOf(113,12,18),arrayOf(145,72,22)),//
                    arrayOf(arrayOf(113,11,19),arrayOf(144,72,23)),//
                    arrayOf(arrayOf(113,12,19),arrayOf(144,72,23)),//
                    arrayOf(arrayOf(113,11,18),arrayOf(144,72,23)),//
                    arrayOf(arrayOf(113,12,18),arrayOf(144,72,23)),//
                    arrayOf(arrayOf(113,11,19),arrayOf(145,72,23)),//
                    arrayOf(arrayOf(113,12,19),arrayOf(145,72,23)),//
                    arrayOf(arrayOf(113,11,18),arrayOf(145,72,23)),//
                    arrayOf(arrayOf(113,12,18),arrayOf(145,72,23))//
//                    arrayOf(arrayOf(75,53,127,196,71,110,192,93))
            )
        }
    }

}