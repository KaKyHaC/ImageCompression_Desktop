package ImageCompressionLib.Utils.Functions

import ImageCompressionLib.Containers.Type.DataOpc
import ImageCompressionLib.Containers.Matrix.ShortMatrix
import ImageCompressionLib.Containers.Type.Size
import ImageCompressionLib.Utils.Functions.OpcAlgorithms.Companion.OpcDirectWithMessageAt
import ImageCompressionLib.Utils.Functions.OpcAlgorithms.Companion.OpcReverceWithMessageAt
import ImageCompressionLib.Utils.Functions.OpcAlgorithms.Companion.OpcReverseDefault
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(value = Parameterized::class)
class OpcAlgorithmsMessageAtTest{

    lateinit var matrix: ShortMatrix
    lateinit var matrixEmpty: ShortMatrix
    lateinit var dataOpc: DataOpc
    var size: Size
    var max:Int
    var range:Int
    var position:Int

    constructor(size: Size, max: Int, range:Int, position: Int) {
        this.size = size
        this.max = max
        this.range=range
        this.position=position
    }
    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0},max={1}, pos={3}")
        fun data(): Collection<Array<Any>> {
            val res = listOf(
                    arrayOf(Size(1, 2), 3,1,1)
                    ,arrayOf(Size(1, 2), 126,1,1)

                    , arrayOf(Size(1, 8), 126,1,0)
                    , arrayOf(Size(1, 8), 126,1,1)
                    , arrayOf(Size(1, 8), 126,1,2)
                    , arrayOf(Size(1, 8), 126,1,3)
                    , arrayOf(Size(1, 8), 126,1,4)
                    , arrayOf(Size(1, 8), 126,1,5)
                    , arrayOf(Size(1, 8), 126,1,6)

                    , arrayOf(Size(2, 2), 126,1,1)
                    , arrayOf(Size(4, 4), 126,1,1)

                    , arrayOf(Size(1, 8), 255,1,1)
                    , arrayOf(Size(1, 8), 255,1,3)
                    , arrayOf(Size(1, 8), 255,1,6)

                    ,arrayOf(Size(8, 1), 126,1,1)
                    ,arrayOf(Size(8, 8), 126,1,1)
            )
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
    fun TrueMessageAuth(){
        val cpy=matrix.copy()
        val message= true
        OpcDirectWithMessageAt(matrix,dataOpc,message,position)
        val res = OpcReverceWithMessageAt(matrixEmpty,dataOpc,position)
        assertEquals(message,res)
        cpy.assertInRange(matrixEmpty,range)
    }
    @Test
    fun FlaseMessageAuth(){
        val cpy=matrix.copy()
        val message= false
        OpcDirectWithMessageAt(matrix,dataOpc,message,position)
        val res = OpcReverceWithMessageAt(matrixEmpty,dataOpc,position)
        assertEquals(message,res)
        cpy.assertInRange(matrixEmpty,range)
    }
    @Test
    fun TrueMessageNotAuth(){
//        println("Not work")
        val cpy=matrix.copy()
        val message= true
        OpcDirectWithMessageAt(matrix,dataOpc,message,position)
        OpcReverseDefault(matrixEmpty,dataOpc)
        cpy.assertInRange(matrixEmpty,range)
    }
    @Test
    fun FlaseMessageNotAuth(){
//        println("Not work")
        val cpy=matrix.copy()
        val message= false
        OpcDirectWithMessageAt(matrix,dataOpc,message,position)
        OpcReverseDefault(matrixEmpty,dataOpc)
        cpy.assertInRange(matrixEmpty,range)
    }


}