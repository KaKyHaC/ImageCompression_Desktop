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
    lateinit var res:ShortMatrix
    internal var size = Size(SIZEOFBLOCK, SIZEOFBLOCK)
    @Before
    @Throws(Exception::class)
    fun setUp() {
        data = ShortMatrix(size){i, j -> (i*j%255).toShort() }
    }

    @Test
    @Throws(Exception::class)
    fun TestDctNotEquals() {
        res = ShortMatrix.valueOf(DCTMultiThread.directDCT(data))
        res = ShortMatrix.valueOf(DCTMultiThread.reverseDCT(data))

        assertFails { data.assertInRange( res, 0)}
    }

    @Test
    @Throws(Exception::class)
    fun TestDCTinRange1() {
        res = ShortMatrix.valueOf(DCTMultiThread.directDCT(data))
        assertFails { data.assertInRange( res, 0)}
        res = ShortMatrix.valueOf(DCTMultiThread.reverseDCT(data))
        data.assertInRange(res,1)
    }


}