package ImageCompressionLib.Utils.Functions.Dct

import ImageCompressionLib.Containers.Matrix.Matrix
import ImageCompressionLib.Containers.Matrix.ShortMatrix
import ImageCompressionLib.Containers.Type.Size
import org.junit.Before

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.util.*

@RunWith(Parameterized::class)
class DctUniversalAlgorithmTest(val size: Size,val range: Int) {
    lateinit var data:Matrix<Short>
    @Before
    fun setUp() {
        data=createRandomMatrix(size,range)
    }
    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0},range:{1}")
        fun data(): Collection<Array<Any>> {
            return listOf(
                    arrayOf(Size(1,2),255)
                    ,arrayOf(Size(2,1),255)
                    ,arrayOf(Size(8,8),255)
                    ,arrayOf(Size(4,4),255)
                    ,arrayOf(Size(20,20),255)
                    ,arrayOf(Size(7,9),255)
                    ,arrayOf(Size(3,8),255)
                    ,arrayOf(Size(8,3),255)
                    ,arrayOf(Size(1,5),255)
                    ,arrayOf(Size(5,1),255)
            )
        }
    }

    @Test
    fun testDtcAlgoritmInRange2(){
        val cpy=ShortMatrix.valueOf(data).copy()
        val convertor=DctUniversalAlgorithm(data.size)
        val tmp=convertor.directDCT(data)
        val res=convertor.reverceDCT(tmp)

        ShortMatrix.valueOf(res).assertInRange(cpy,2)
    }

    fun createRandomMatrix(size: Size, range:Int): Matrix<Short> {
        return Matrix<Short>(size){i, j ->
            Math.abs(Random().nextInt(range)).toShort()
        }
    }
}