package ImageCompressionLib.Utils.Objects

import ImageCompressionLib.Containers.*
import ImageCompressionLib.Containers.Matrix.ShortMatrix
import ImageCompressionLib.Containers.Type.Flag
import ImageCompressionLib.Containers.Type.Size
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
        @Parameterized.Parameters(name = "Image:{1},unit{2},flag{0}")
        fun data(): Collection<Array<Any>> {
            val flag= Flag.createDefaultFlag()
            val flagSB= Flag.createDefaultFlag()
            flagSB.setTrue(Flag.Parameter.GlobalBase)
            val imageSize= Size(8, 8)
            val unitSize= Size(8, 8)
            val sameBaseSize= Size(2, 2)

            return listOf(
                    arrayOf(flag, Size(128,128),unitSize, sameBaseSize),
                    arrayOf(flag, imageSize, unitSize, sameBaseSize) ,
                    arrayOf(flagSB, imageSize, unitSize, sameBaseSize) ,
                    arrayOf(flag, Size(128,128),Size(7,7), sameBaseSize))
        }
    }

    @Test
    fun testDefaltAlgorithm(){
        val convertor=OpcConvertor(shortMatrix.toArrayShort(),parameters)
        val d=convertor.getDataOpcs()
        val convertor1=OpcConvertor(d,parameters)
        val res=convertor1.getDataOrigin()
        shortMatrixCpy.assertInRange(ShortMatrix.valueOf(res),0)
    }
}