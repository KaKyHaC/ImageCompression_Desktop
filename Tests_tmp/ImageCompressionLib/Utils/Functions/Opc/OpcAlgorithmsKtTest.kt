package ImageCompressionLib.Utils.Functions.Opc

import ImageCompressionLib.Data.Matrix.ShortMatrix
import ImageCompressionLib.Data.Type.DataOpc
import ImageCompressionLib.Data.Primitives.Size
import ImageCompressionLib.Utils.Functions.Opc.OpcAlgorithms.Companion.OpcDirectDefault
import ImageCompressionLib.Utils.Functions.Opc.OpcAlgorithms.Companion.OpcDirectLongAndBI
import ImageCompressionLib.Utils.Functions.Opc.OpcAlgorithms.Companion.OpcDirectUseOnlyLong
import ImageCompressionLib.Utils.Functions.Opc.OpcAlgorithms.Companion.OpcReverseDefault
import ImageCompressionLib.Utils.Functions.Opc.OpcAlgorithms.Companion.OpcReverseUseOnlyLong
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.math.BigInteger
import java.util.*

@RunWith(value = Parameterized::class)
class OpcAlgorithmsKtTest {
    lateinit var matrix: ShortMatrix
    lateinit var matrixEmpty: ShortMatrix
    lateinit var dataOpc: DataOpc
    var size= Size(8, 1)
    var max=255;

    constructor(size: Size, max: Int) {
        this.size = size
        this.max = max
    }
    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun data(): Collection<Array<Any>> {
            val res = listOf(arrayOf(Size(1, 1), 255)
                    , arrayOf(Size(2, 2), 20)
                    , arrayOf(Size(8, 1), 255)
                    , arrayOf(Size(1, 8), 255)
                    , arrayOf(Size(8, 8), 255)
                    , arrayOf(Size(10, 10), 255)
                    , arrayOf(Size(15, 18), 255)
                    , arrayOf(Size(50, 50), 255)
                    , arrayOf(Size(61, 45), 500))
            return res
        }
    }


    @Before
    fun setUp(){
        dataOpc= DataOpc(size)
        matrix= ShortMatrix(size.width, size.height) { i, j -> (Math.abs(Random().nextInt(max))).toShort() }
        OpcUtils.FindBase(matrix,dataOpc)
        matrixEmpty= ShortMatrix(size.width, size.height)
    }

    @Test
    fun testOPCDefault() {
        val cpy=matrix.copy()
        OpcDirectDefault(matrix,dataOpc)
        OpcReverseDefault(matrixEmpty,dataOpc)

        assertEquals(matrixEmpty,cpy)

    }

    @Test
    fun testOPCLongAndBI() {
        val cpy=matrix.copy()
        OpcDirectLongAndBI(matrix,dataOpc)
        OpcReverseDefault(matrixEmpty,dataOpc)
        assertEquals(matrixEmpty,cpy)
    }


    @Test
    fun testOPCOnlyLong() {
        val cpy=matrix.copy()
        OpcDirectUseOnlyLong(matrix,dataOpc)
        OpcReverseUseOnlyLong(matrixEmpty,dataOpc)
        assertEquals(matrixEmpty,cpy)
    }

    @Test
    fun testOPCDefaultMulti2() {
        println("not work")
        val cpy=matrix.copy()
        OpcDirectDefault(matrix,dataOpc)
        dataOpc.N/= BigInteger.TWO
        OpcReverseDefault(matrixEmpty,dataOpc)

        println(cpy)
        println(matrixEmpty)
        assertEquals(matrixEmpty,cpy)
    }
/*
    @Test
    fun testForSpesialArrayWhenNDiv2() {
        println("not work")
        matrix= ShortMatrix(1,3){ i, j -> testArrayTry[j].toShort() }
        matrixEmpty= ShortMatrix(1,3)
        val cpy=matrix.copy()
        dataOpc= DataOpc(Size(1,3))
        OpcUtils.FindBase(matrix,dataOpc)
        OpcDirectDefault(matrix,dataOpc)
        dataOpc.N/= BigInteger.TWO
        OpcReverseDefault(matrixEmpty,dataOpc)

        println(cpy)
        println(matrixEmpty)
        assertEquals(cpy,matrixEmpty)
    }
    val testArray= arrayOf<Int>(122,70,124,178,210,188,48,19)
    val testArrayTry= arrayOf<Int>(122,55,127,178,210,188,48,19)
    val testArrayWork= arrayOf<Int>(75,53,127,196,71,110,192,93)*/
}