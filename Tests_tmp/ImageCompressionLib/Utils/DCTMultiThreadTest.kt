package ImageCompressionLib.Utils

import ImageCompressionLib.Utils.Functions.DCTMultiThread
import org.junit.Before
import org.junit.Test

import java.util.Random

import org.junit.Assert.*
import kotlin.test.assertFails

class DCTMultiThreadTest {
    internal var data: Array<ShortArray> = Array(0,{x->ShortArray(0)})
    internal var res: Array<ShortArray> = Array(0,{x->ShortArray(0)})
    internal var size = DCTMultiThread.SIZEOFBLOCK
    @Before
    @Throws(Exception::class)
    fun setUp() {
        data = Array(size) { ShortArray(size) }
        for (i in 0 until size) {
            for (j in 0 until size) {
                data[i][j] = (i * j).toShort()
            }
        }
    }

    @Test
    @Throws(Exception::class)
    fun TestDctNotEquals() {
        res = DCTMultiThread.directDCT(data)
        res = DCTMultiThread.reverseDCT(res)

        //        assertArrayEquals(res,data);
        assertFails { assertArrayInRange(data, res, 0)}
    }

    @Test
    @Throws(Exception::class)
    fun TestDCTinRange1() {
        res = DCTMultiThread.directDCT(data)
        assertFails { assertArrayInRange(data,res,1) }

        res = DCTMultiThread.reverseDCT(res)
        assertArrayInRange(data, res, 1)
    }

    @Test
    @Throws(Exception::class)
    fun MultiDCT() {
        val random = Random()
        for (i in 0..9) {
            for (x in 0 until size) {
                for (y in 0 until size) {
                    data[x][y] = random.nextInt(255).toShort()
                }
            }
            res = DCTMultiThread.directDCT(data)
            assertFails { assertArrayInRange(data,res,2) }

            res = DCTMultiThread.reverseDCT(res)
            assertArrayInRange(data, res, 2)
        }
    }

    private fun assertArrayInRange(a: Array<ShortArray>, b: Array<ShortArray>, delta: Int) {
        assertEquals(a.size.toLong(), b.size.toLong())
        assertTrue(a.size > 0)
        assertEquals(a[0].size.toLong(), b[0].size.toLong())

        for (i in a.indices) {
            for (j in 0 until a[0].size) {
                var isEqual = false
                for (d in -delta..delta) {
                    if (a[i][j].toInt() == b[i][j] + d)
                        isEqual = true
                }
                assertTrue("in [" + i + "][" + j + "] value " + a[i][j] + "!=" + b[i][j], isEqual)
            }
        }
    }

}