package ImageCompressionLib.ProcessingModules

import ImageCompressionLib.Constants.State
import ImageCompressionLib.Containers.Matrix.ShortMatrix
import ImageCompressionLib.Containers.Parameters
import ImageCompressionLib.Containers.TripleShortMatrix
import ImageCompressionLib.Containers.Type.Flag
import ImageCompressionLib.Utils.Objects.TimeManager
import Utils.BuffImConvertor
import org.junit.Test

import java.awt.image.BufferedImage
import java.util.Random

import org.junit.Assert.*
import org.junit.Before
import kotlin.test.assertFails

class ModuleImageTest {
    internal var param =Parameters.createParametresForTest()
    @Before
    fun befor(){
        param =Parameters.createParametresForTest()
//        param = Flag()
    }

    @Test
    fun TestDefault5() {
        var mybufferedImage= randomBufferedImage(122,124)
        val bufferedImage=BuffImConvertor.instance.convert(mybufferedImage)

        var w=bufferedImage.width
        var h=bufferedImage.height

        val myBufferedImage = ModuleImage(bufferedImage, param)
        val matrix = myBufferedImage.getYenlMatrix(true)

        val myBufferedImage1 = ModuleImage(matrix, param)
        val bufferedImage1 = myBufferedImage1.bufferedImage

        val arr1 = bufferedImage.getIntArray()//?.getRGB(0, 0, w, h, null, 0, w)
        val arr2 = bufferedImage1.getIntArray()//.getRGB(0, 0, w, h, null, 0, w)

        assertArrayInRange(arr1, arr2, 5)
    }

    @Test
    fun TestEnlagment4() {
        val grad=getGradientMatrix(124,546)
        param.flag.setChecked(Flag.Parameter.Enlargement,true)

        val mi= ModuleImage(grad, param)
        val bmp=mi.rgbMatrixOld.copy()
        var enl=mi.getYenlMatrix(true)
        assertFails { assertTrue(bmp.assertMatrixInRange(enl,1)) }

        val mi1= ModuleImage(enl, param)
        val bmp1=mi1.rgbMatrixOld
        kotlin.test.assertTrue { bmp1.assertMatrixInRange(bmp,4) }

    }

    //TODO time tests
    @Test
    fun TimeTest1(){
        BmpToYenlTimeTest(1920,1080,3)
    }
    @Test
    fun TimeTest2(){
        BmpToYenlTimeTest(1920,1080,30)
    }
    @Test
    fun TimeTest3(){
        BmpToYenlTimeTest(4920,4080,1)
    }

    fun BmpToYenlTimeTest(w:Int,h:Int,loop:Int){
        val bufferedImage= randomBufferedImage(w,h)
        val mBI=BuffImConvertor.instance.convert(bufferedImage)
        val w=bufferedImage.width
        val h=bufferedImage.height

        TimeManager.Instance.startNewTrack("m BmpToYenl ${loop}l ${w}x$h")
        for(i in 0..loop-1) {
            val myBufferedImage = ModuleImage(mBI, param)
            val matrix = myBufferedImage.getYenlMatrix(true)
        }
        TimeManager.Instance.append("multi")
        println(TimeManager.Instance.getInfoInSec())
        val t1=TimeManager.Instance.getTotalTime()
        TimeManager.Instance.startNewTrack("o BmpToYenl ${loop}l ${w}x$h")
        for(i in 0..loop-1) {
            val myBufferedImage = ModuleImage(mBI, param)
            val matrix = myBufferedImage.getYenlMatrix(false)
        }
        TimeManager.Instance.append("one")
        println(TimeManager.Instance.getInfoInSec())
        val t2=TimeManager.Instance.getTotalTime()
        println("multi $t1;one $t2")
        assertTrue(t2>t1)

    }
    fun getGradientMatrix(w:Int,h:Int): TripleShortMatrix {
        val a=ShortMatrix(w,h){i, j -> (i*j%255).toShort() }
        val b=ShortMatrix(w,h){i, j -> (i*j%255).toShort() }
        val c=ShortMatrix(w,h){i, j -> (i*j%255).toShort() }
        return TripleShortMatrix(a,b,c,param,State.RGB)

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