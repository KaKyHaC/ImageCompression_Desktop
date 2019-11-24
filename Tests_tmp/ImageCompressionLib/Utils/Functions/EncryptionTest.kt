package ImageCompressionLib.Utils.Functions

import ImageCompressionLib.Data.Matrix.DataOpcMatrix
import ImageCompressionLib.Data.Parameters
import ImageCompressionLib.Data.TripleDataOpcMatrix
import ImageCompressionLib.Data.Type.Size
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.util.*

@RunWith(Parameterized::class)
class EncryptionTest(val imageSize:Size,val unitSize: Size,val key:String) {

    lateinit var dopcs:TripleDataOpcMatrix
    @Before
    fun setUp(){
        val param=Parameters.createParametresForTest(imageSize,unitSize)
        val a=DataOpcMatrix(param.matrixOfUnitSize.width,param.matrixOfUnitSize.height,unitSize)
        a.forEach { i, j, value ->
            for(q in 0 until unitSize.height)
                value.base[q]=Math.abs(Random().nextInt(255)).toShort()
            return@forEach value
        }
        dopcs= TripleDataOpcMatrix(a,a,a,param)
    }

    @Test
    fun encode() {
        val cpy=dopcs.copy()

        val tmp=Encryption.encode(dopcs,key)
        assertNotEquals(cpy,tmp)

        val res=Encryption.encode(tmp,key)
        assertEquals(res,cpy)
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data(): Collection<Array<Any>> {
            return listOf(
                    arrayOf(Size(8,8),Size(8,8),"abc"),
                    arrayOf(Size(128,128),Size(8,8),"abc"),
                    arrayOf(Size(538,721),Size(7,9),"a32mfbc")
            )

        }
    }
}