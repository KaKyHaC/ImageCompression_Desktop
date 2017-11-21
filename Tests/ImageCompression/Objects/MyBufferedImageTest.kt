package ImageCompression.Objects

import ImageCompression.Constants.State
import ImageCompression.Containers.Matrix
import ImageCompression.Utils.Objects.Flag
import org.junit.Before
import org.junit.Test

import java.awt.image.BufferedImage
import java.util.Random

import org.junit.Assert.*
import kotlin.test.assertFails

class MyBufferedImageTest {
    internal var flag = Flag("0")

    @Test
    @Throws(Exception::class)
    fun TestDefault() {
        var bufferedImage= randomBufferedImage(122,123)
        var w=bufferedImage.width
        var h=bufferedImage.height

        val myBufferedImage = MyBufferedImage(bufferedImage, flag)
        val matrix = myBufferedImage.yCbCrMatrix

        val myBufferedImage1 = MyBufferedImage(matrix)
        val bufferedImage1 = myBufferedImage1.bufferedImage

        val arr1 = bufferedImage?.getRGB(0, 0, w, h, null, 0, w)
        val arr2 = bufferedImage1!!.getRGB(0, 0, w, h, null, 0, w)

        assertArrayInRange(arr1, arr2, 2)
    }

    @Test
    fun TestEnlagment4() {
        val grad=getGradientMatrix(124,546)
        grad.f.isEnlargement=true

        val mi=MyBufferedImage(grad)
        val bmp=mi.rgbMatrix.copy()
        var enl=mi.yenlMatrix
        assertFails { assertTrue(bmp.assertMatrixInRange(enl,1)) }

        val mi1=MyBufferedImage(enl)
        val bmp1=mi1.rgbMatrix
        kotlin.test.assertTrue { bmp1.assertMatrixInRange(bmp,4) }

    }
    fun getGradientMatrix(w:Int,h:Int):Matrix{
        var m=Matrix(w,h,Flag(0),State.RGB)
        forEach(w,h, { x, y ->
            run {
                m.a[x][y] = ((x + y)%255).toShort()
                m.b[x][y] = ((x + y)%255).toShort()
                m.c[x][y] = ((x + y)%255).toShort()
            }
        })
        return m
    }


    private fun forEach(w: Int, h: Int, `fun`: (x:Int,y:Int)->Unit) {
        for (i in 0 .. w-1) {
            for (j in 0 .. h-1) {
                `fun`.invoke(i, j)
            }
        }
    }

    companion object {
        fun randomBufferedImage(w: Int,h: Int): BufferedImage {
            val random = Random()
            var res=BufferedImage(w,h,BufferedImage.TYPE_3BYTE_BGR)
            for (i in 0 until w) {
                for (j in 0 until h) {
                    val `val` = random.nextInt(16000000)
                    res.setRGB(i, j, `val`)
                }
            }
            return res
        }

        fun assertArrayInRange(ar1: IntArray, ar2: IntArray, delta: Int) {
            assertEquals(ar1.size.toLong(), ar2.size.toLong())
            assertTrue(ar1.size > 0)

            for (i in ar1.indices) {
                var isEqual = false
                var isR = false
                var isG = false
                var isB = false

                val r1: Int
                val g1: Int
                val b1: Int
                val r2: Int
                val g2: Int
                val b2: Int
                r1 = ar1[i] shr 16 and 0xFF
                g1 = ar1[i] shr 8 and 0xFF
                b1 = ar1[i] and 0xFF
                r2 = ar2[i] shr 16 and 0xFF
                g2 = ar2[i] shr 8 and 0xFF
                b2 = ar2[i] and 0xFF
                for (d in -delta..delta) {
                    if (r1 == r2 + d)
                        isR = true
                    if (g1 == g2 + d)
                        isG = true
                    if (b1 == b2 + d)
                        isB = true
                }
                isEqual = isR && isG && isB
                assertTrue("in [" + i + "] value " + ar1[i] + "!=" + ar2[i], isEqual)
            }

        }
    }

}