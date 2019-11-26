package ImageCompressionLib.Utils.Functions.Opc

import ImageCompressionLib.Containers.Type.DataOpc
import ImageCompressionLib.Containers.Matrix.ShortMatrix
import ImageCompressionLib.Containers.Type.Size
import ImageCompressionLib.Utils.Functions.Opc.OpcAlgorithms.Companion.opcDirectWithMessageAtFirst
import ImageCompressionLib.Utils.Functions.Opc.OpcAlgorithms.Companion.opcReverseWithMessageAtFirst
import ImageCompressionLib.Utils.Functions.Opc.OpcAlgorithms.Companion.opcReverseDefault
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(value = Parameterized::class)
class OpcAlgorithmsMessageTest {

    lateinit var matrix: ShortMatrix
    lateinit var matrixEmpty: ShortMatrix
    lateinit var dataOpc: DataOpc
    var size: Size
    var max:Int
    var range:Int

    constructor(size: Size, max: Int, range:Int) {
        this.size = size
        this.max = max
        this.range=range
    }
    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0},range={2},max={1}")
        fun data(): Collection<Array<Any>> {
            val res = listOf(
                    arrayOf(Size(8, 8), 255,0)
                    , arrayOf(Size(8, 1), 255,0)
                    , arrayOf(Size(1, 8), 255,0))
            return res
        }
    }


    @Before
    fun setUp(){
        dataOpc= DataOpc(size)
        for(i in 0 until size.height){
            dataOpc.base[i]=(max+1).toShort()
        }
        matrix= ShortMatrix(size.width, size.height) { i, j -> ((i + 1) * (j + 1) % max).toShort() }
        matrixEmpty= ShortMatrix(size.width, size.height)
    }

    @Test
    fun testOPCWithMessageTrueAtFirstInit() {
        val cpy=matrix.copy()
        val message= true
        opcDirectWithMessageAtFirst(matrix,dataOpc,message)
        val res = opcReverseWithMessageAtFirst(matrixEmpty,dataOpc)
        assertEquals(message,res)
        cpy.assertInRange(matrixEmpty,range)
    }
    @Test
    fun testOPCWithMessageFalseAtFirstInit() {
        val cpy=matrix.copy()
        val message= false
        opcDirectWithMessageAtFirst(matrix,dataOpc,message)
        val res = opcReverseWithMessageAtFirst(matrixEmpty,dataOpc)
        assertEquals(message,res)
        cpy.assertInRange(matrixEmpty,range)
    }
    @Test
    fun testOPCWithMessageTrueAtFirstNotInit() {
        val cpy=matrix.copy()
        val message= true
        opcDirectWithMessageAtFirst(matrix,dataOpc,message)
        opcReverseDefault(matrixEmpty,dataOpc)
        cpy.assertInRange(matrixEmpty,range)
    }
    @Test
    fun testOPCWithMessageFalseAtFirstNotInit() {
        val cpy=matrix.copy()
        val message= false
        opcDirectWithMessageAtFirst(matrix,dataOpc,message)
        opcReverseDefault(matrixEmpty,dataOpc)
        cpy.assertInRange(matrixEmpty,range)
    }
}