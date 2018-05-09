package ImageCompressionLib.Utils

import ImageCompressionLib.Constants.SIZEOFBLOCK
import ImageCompressionLib.Containers.Matrix.ShortMatrix
import ImageCompressionLib.Containers.Type.Size
import ImageCompressionLib.Utils.Functions.DCTMultiThread
import org.junit.Before
import org.junit.Test

import java.util.Random

import org.junit.Assert.*
import kotlin.test.assertFails

class DCTMultiThreadTest {
    lateinit var data:ShortMatrix
//    lateinit var res:ShortMatrix
    internal var size = Size(SIZEOFBLOCK, SIZEOFBLOCK)
    @Before
    @Throws(Exception::class)
    fun setUp() {
        data = ShortMatrix(size){i, j -> (Math.abs(Random().nextInt())%255).toShort() }
    }

    @Test
    @Throws(Exception::class)
    fun TestDctNotEquals() {
        var res = ShortMatrix.valueOf(DCTMultiThread.directDCT(data))
        res = ShortMatrix.valueOf(DCTMultiThread.reverseDCT(data))

        assertFails { data.assertInRange( res, 0)}
    }

    @Test
    @Throws(Exception::class)
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


}