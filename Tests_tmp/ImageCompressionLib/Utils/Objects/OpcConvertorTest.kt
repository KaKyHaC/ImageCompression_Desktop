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
class OpcConvertorTest(val parameters: Parameters) {
    lateinit var shortMatrix: ShortMatrix
    lateinit var shortMatrixCpy: ShortMatrix
    @Before
    fun setUp() {
        shortMatrix= ShortMatrix(parameters.imageSize.width, parameters.imageSize.height) { i, j -> ((i + 1) * (j + 1) % 255).toShort() }
        shortMatrixCpy=shortMatrix.copy()
    }
    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data(): Collection<Array<Any>> {
            val flag= Flag.createDefaultFlag()
            val imageSize= Size(128, 128)
            val unitSize= Size(8, 8)
            val sameBaseSize= Size(1, 1)

            return listOf(arrayOf(Parameters(flag, imageSize, unitSize, sameBaseSize) as Any))
        }
    }

    @Test
    fun testDefaltAlgorithm(){
        val convertor=OpcConvertor(shortMatrix.toArrayShort(),parameters)
        val d=convertor.getDataOpcs()
        val convertor1=OpcConvertor(d,parameters)
        val res=convertor1.getDataOrigin()
        shortMatrixCpy.assertInRange(ShortMatrix.valueOf(res),1)
    }
}