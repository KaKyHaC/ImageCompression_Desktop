package ImageCompressionLib.Utils

import ImageCompressionLib.Constants.SIZEOFBLOCK
import ImageCompressionLib.Containers.Matrix.ShortMatrix
import ImageCompressionLib.Containers.Type.Size
import ImageCompressionLib.Utils.Functions.DCTMultiThread
import org.junit.Before
import org.junit.Test

import java.util.Random

import org.junit.Assert.*
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.test.assertFails

@RunWith(Parameterized::class)
class DCTMultiThreadTest(val size:Size) {
    lateinit var data:ShortMatrix
    @Before
    fun setUp() {
        data = ShortMatrix(size){i, j -> (Math.abs(Random().nextInt())%255).toShort() }
    }

    @Test
    fun TestDctNotEquals() {
        var res = ShortMatrix.valueOf(DCTMultiThread.directDCT(data))
        res = ShortMatrix.valueOf(DCTMultiThread.reverseDCT(data))

        assertFails { data.assertInRange( res, 0)}
    }

    @Test
    fun TestDCTinRange1() {
        val cpy=data.copy()
        val dir = ShortMatrix.valueOf(DCTMultiThread.directDCT(data))
        assertFails { cpy.assertInRange( dir, 0)}
        val rev = ShortMatrix.valueOf(DCTMultiThread.reverseDCT(dir))
        cpy.assertInRange(rev,1)
    }
    @Test
    @Throws(Exception::class)
    fun TestDCTinRange3() {
        val cpy=data.copy()
        val dir = ShortMatrix.valueOf(DCTMultiThread.directDCT(data))
        assertFails { cpy.assertInRange( dir, 0)}
        val rev = ShortMatrix.valueOf(DCTMultiThread.reverseDCT(dir))
        cpy.assertInRange(rev,3)
    }
    companion object{
        @JvmStatic
        @Parameterized.Parameters(name="{0}")
        fun data(): Collection<Array<Size>> {
            return listOf(
                    arrayOf(Size(8,8))
//                    ,arrayOf(Size(7,7))
//                    ,arrayOf(Size(7,9))
            )
        }
    }


}