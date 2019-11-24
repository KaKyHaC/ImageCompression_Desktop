package ImageCompressionLib.Utils.Functions.Opc

import ImageCompressionLib.Data.Matrix.ShortMatrix
import ImageCompressionLib.Data.Primitives.DataOpc
import ImageCompressionLib.Data.Primitives.Size
import org.junit.Before

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.math.BigInteger
import java.util.*

@RunWith(Parameterized::class)
class OpcUtilsOddAndEvenTest(val size: Size) {
    lateinit var matrix:ShortMatrix
    lateinit var dataOpc: DataOpc
    val cpyOpc: DataOpc
    val cpyData:ShortMatrix

    init {
        cpyOpc = DataOpc(size)
        for(i in 0 until size.height)
            cpyOpc.base[i]=Math.abs(Random().nextInt(255)).toShort()
        cpyData= ShortMatrix(size){ i, j -> Math.abs(Random().nextInt(cpyOpc.base[j].toInt())).toShort() }

    }
    @Before
    fun setUp() {
        matrix=cpyData.copy()
        dataOpc=cpyOpc.copy()
    }
    @Test
    fun TestEvenBaseRandData(){
        OpcUtils.MakeBaseEven(dataOpc)
        println(matrix)
        OpcAlgorithms.OpcDirectDefault(matrix,dataOpc)
        dataOpc.N/= BigInteger.TWO
        OpcAlgorithms.OpcReverseDefault(matrix,dataOpc)
        println(dataOpc)
        println(matrix)
        assertEquals(cpyData,matrix)
    }
    @Test
    fun TestRandBaseOddData(){
        OpcUtils.MakeDataOdd(matrix)
        println(matrix)
        OpcAlgorithms.OpcDirectDefault(matrix,dataOpc)
        dataOpc.N/= BigInteger.TWO
        OpcAlgorithms.OpcReverseDefault(matrix,dataOpc)
        println(dataOpc)
        println(matrix)
        assertEquals(cpyData,matrix)
    }
    @Test
    fun TestEvenBaseOddData(){
        OpcUtils.MakeBaseEven(dataOpc)
        OpcUtils.MakeDataOdd(matrix)
        println(matrix)
        OpcAlgorithms.OpcDirectDefault(matrix,dataOpc)
        dataOpc.N/= BigInteger.TWO
        OpcAlgorithms.OpcReverseDefault(matrix,dataOpc)
        println(dataOpc)
        println(matrix)
        assertEquals(cpyData,matrix)
    }
    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data(): Collection<Array<Size>> {
            return listOf(
                    arrayOf(Size(1, 8))
            )
        }
    }
}