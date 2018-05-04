package ImageCompressionLib.Containers

import ImageCompressionLib.Containers.Matrix.ShortMatrix
import ImageCompressionLib.Containers.Type.Size
import org.junit.Before
import org.junit.Test

class ShortMatrixTest {
    lateinit var shortMatrix: ShortMatrix
    @Before
    fun setUp() {
        val size= Size(8, 8)
        shortMatrix= ShortMatrix(size) { i, j -> (i * j).toShort() }
    }

    @Test
    fun valueOfMatrix() {
        val tmp=shortMatrix.toMatrix()
        val res= ShortMatrix.valueOf(tmp)
        shortMatrix.assertInRange(res,0)
    }

    @Test
    fun valueOfArray() {
        val tmp=shortMatrix.toShortArray()
        val res= ShortMatrix.valueOf(tmp)
        shortMatrix.assertInRange(res,0)
    }
}