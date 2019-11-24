package ImageCompressionLib.Utils.Convertors

import ImageCompressionLib.Data.*
import ImageCompressionLib.Data.Matrix.DataOpcMatrix
import ImageCompressionLib.Data.Matrix.ShortMatrix
import ImageCompressionLib.Data.Type.Flag
import ImageCompressionLib.Data.Type.Size
import org.junit.Before

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(value = Parameterized::class)
class OpcConvertorTest(flag: Flag,imageSize: Size,unitSize: Size,baseSize: Size) {
    val parameters: Parameters= Parameters(flag, imageSize, unitSize,baseSize)
    lateinit var shortMatrix: ShortMatrix
    lateinit var shortMatrixCpy: ShortMatrix

    @Before
    fun setUp() {
        shortMatrix= ShortMatrix(parameters.imageSize.width, parameters.imageSize.height) { i, j -> ((i + 1) * (j + 1) % 255).toShort() }
        shortMatrixCpy=shortMatrix.copy()
    }
    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "Image:{1},unit{2},param{0}")
        fun data(): Collection<Array<Any>> {
            val flag= Flag.createDefaultFlag()
            val flagSB= Flag.createDefaultFlag()
            flagSB.setTrue(Flag.Parameter.GlobalBase)
            val imageSize= Size(8, 8)
            val unitSize= Size(8, 8)
            val sameBaseSize= Size(2, 2)

            return listOf(
//                    arrayOf(flag, Size(128,128),unitSize, sameBaseSize),
//                    arrayOf(flag, imageSize, unitSize, sameBaseSize) ,
                    arrayOf(flagSB, Size(123,243), unitSize, sameBaseSize) ,
                    arrayOf(flag, Size(128,128),Size(7,7), sameBaseSize),
                    arrayOf(flag, Size(128,128),Size(8,8), sameBaseSize))
        }
    }

    @Test
    fun testDefaultAlgorithm(){
        val convertor=OpcConvertor(shortMatrix.toArrayShort(),parameters)
        val d=convertor.getDataOpcs(null,null)
        val convertor1=OpcConvertor(DataOpcMatrix.valueOf(d),parameters)
        val res=convertor1.getDataOrigin().first
        val sres=ShortMatrix.valueOf(res.rectBuffer(0,0,shortMatrixCpy.size))
        shortMatrixCpy.assertInRange(sres,0)
    }
}