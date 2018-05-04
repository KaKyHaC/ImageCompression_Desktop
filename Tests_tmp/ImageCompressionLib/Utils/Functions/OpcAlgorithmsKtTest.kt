package ImageCompressionLib.Utils.Functions

import ImageCompressionLib.Containers.Matrix.ShortMatrix
import ImageCompressionLib.Containers.Type.DataOpc
import ImageCompressionLib.Containers.Type.Size
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
        @Parameterized.Parameters
        fun data(): Collection<Array<Any>> {
            val res = listOf(arrayOf(Size(1, 1), 255)
                    , arrayOf(Size(1, 1), 255)
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
        for(i in 0 until size.height){
            dataOpc.base[i]=(max+1).toShort()
        }
        matrix= ShortMatrix(size.width, size.height) { i, j -> (i * j % max).toShort() }
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

}