package ImageCompressionLib.Utils.Functions.Opc

import ImageCompressionLib.Data.Matrix.ShortMatrix
import ImageCompressionLib.Data.Type.DataOpc
import ImageCompressionLib.Data.Type.Size
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.math.BigInteger

@RunWith(Parameterized::class)
class OpcAlgirithmsCodeDiv2(val array:Array<Int>) {

    @Test
    fun testForSpesialArrayWhenNDiv2() {
        println("not work")
        val matrix= ShortMatrix(1,8){ i, j -> array[j].toShort() }
        val matrixEmpty= ShortMatrix(1,8)
        val cpy=matrix.copy()
        val dataOpc= DataOpc(Size(1,8))
        OpcUtils.FindBase(matrix,dataOpc)
        OpcAlgorithms.OpcDirectDefault(matrix, dataOpc)
        dataOpc.N/= BigInteger.TWO
        OpcAlgorithms.OpcReverseDefault(matrixEmpty, dataOpc)

        println(cpy)
        println(matrixEmpty)
        Assert.assertEquals(cpy, matrixEmpty)
    }
    @Test
    fun makeBaseEven() {
        println("not work")
        val matrix= ShortMatrix(1,8){ i, j -> array[j].toShort() }
        val matrixEmpty= ShortMatrix(1,8)
        val cpy=matrix.copy()
        val dataOpc= DataOpc(Size(1,8))
        OpcUtils.FindBase(matrix,dataOpc)
        OpcUtils.MakeBaseEven(dataOpc)
        OpcAlgorithms.OpcDirectDefault(matrix, dataOpc)
        dataOpc.N/= BigInteger.TWO
        OpcAlgorithms.OpcReverseDefault(matrixEmpty, dataOpc)

        println(cpy)
        println(matrixEmpty)
        Assert.assertEquals(cpy, matrixEmpty)
    }
    @Test
    fun makeDataOdd() {
        println("not work")
        val matrix= ShortMatrix(1,8){ i, j -> array[j].toShort() }
        OpcUtils.MakeDataOdd(matrix)
        val matrixEmpty= ShortMatrix(1,8)
        val cpy=matrix.copy()
        val dataOpc= DataOpc(Size(1,8))
        OpcUtils.FindBase(matrix,dataOpc)
        OpcAlgorithms.OpcDirectDefault(matrix, dataOpc)
        dataOpc.N/= BigInteger.TWO
        OpcAlgorithms.OpcReverseDefault(matrixEmpty, dataOpc)

        println(cpy)
        println(matrixEmpty)
        Assert.assertEquals(cpy, matrixEmpty)
    }
    @Test
    fun OddDataAndRandBase(){
        println("not work")
        val matrix= ShortMatrix(1,8){ i, j -> array[j].toShort() }
        OpcUtils.MakeDataOdd(matrix)
        val matrixEmpty= ShortMatrix(1,8)
        val cpy=matrix.copy()
        val dataOpc= DataOpc(Size(1,8))
        OpcUtils.FindBase(matrix,dataOpc)
        for(i in 0 until 8){
            dataOpc.base[i]=(dataOpc.base[i]+i).toShort()
        }
        OpcAlgorithms.OpcDirectDefault(matrix, dataOpc)
        dataOpc.N/= BigInteger.TWO
        OpcAlgorithms.OpcReverseDefault(matrixEmpty, dataOpc)

        println(cpy)
        println(matrixEmpty)
        Assert.assertEquals(cpy, matrixEmpty)
    }
    /*@Test
    fun testBaseAndRandomData() {
        println("not work")
        val dataOpc= DataOpc(Size(1,8))
        for(i in 0 until 8)
            dataOpc.base[i]=(array[i]+1).toShort()
        val matrix= ShortMatrix(1,8){ i, j -> Math.abs(Random().nextInt(array[j])).toShort() }
        val matrixEmpty= ShortMatrix(1,8)
        val cpy=matrix.copy()
        OpcUtils.FindBase(matrix,dataOpc)
        OpcAlgorithms.OpcDirectDefault(matrix, dataOpc)
        dataOpc.N/= BigInteger.TWO
        OpcAlgorithms.OpcReverseDefault(matrixEmpty, dataOpc)

        println(cpy)
        println(matrixEmpty)
        Assert.assertEquals(cpy, matrixEmpty)
    }
    @Test
    fun testRandomBaseAndData() {
        println("not work")
        val dataOpc= DataOpc(Size(1,8))
        val matrix= ShortMatrix(1,8){ i, j -> array[j].toShort() }
        for(i in 0 until 8)
            dataOpc.base[i]=(array[i]+i).toShort()
        val matrixEmpty= ShortMatrix(1,8)
        val cpy=matrix.copy()
        OpcUtils.FindBase(matrix,dataOpc)
        OpcAlgorithms.OpcDirectDefault(matrix, dataOpc)
        dataOpc.N/= BigInteger.TWO
        OpcAlgorithms.OpcReverseDefault(matrixEmpty, dataOpc)

        println(cpy)
        println(matrixEmpty)
        Assert.assertEquals(cpy, matrixEmpty)
    }*/
    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun data(): Collection<Array<Array<Int>>> {
            return listOf(
                    arrayOf(arrayOf(122,70,124,178,210,188,48,19)),
                    arrayOf(arrayOf(122,70,124,178,210,188,49,19)),
                    arrayOf(arrayOf(122,70,124,178,210,189,48,19)),
                    arrayOf(arrayOf(122,70,124,178,211,188,48,19)),
                    arrayOf(arrayOf(122,70,124,179,210,188,48,19)),
                    arrayOf(arrayOf(122,70,125,178,210,188,48,19)),
                    arrayOf(arrayOf(122,71,124,178,210,188,48,19)),
                    arrayOf(arrayOf(123,70,124,178,210,188,48,19)),
//                    arrayOf(arrayOf(122,65,124,178,210,188,48,19)),
//                    arrayOf(arrayOf(122,60,124,178,210,188,48,19)),
//                    arrayOf(arrayOf(122,55,124,178,210,188,48,19)),
//                    arrayOf(arrayOf(100,50,124,178,210,188,48,19)),
//                    arrayOf(arrayOf(90,50,124,178,210,188,48,19)),
//                    arrayOf(arrayOf(80,50,124,178,210,188,48,19)),
//                    arrayOf(arrayOf(71,50,124,178,210,188,48,19)),
//                    arrayOf(arrayOf(123,70,124,178,210,188,48,19)),
//                    arrayOf(arrayOf(122,71,124,178,210,188,48,19)),
                    arrayOf(arrayOf(75,53,127,196,71,110,192,93))
            )
        }
    }

}