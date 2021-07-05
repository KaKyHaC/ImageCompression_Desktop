package ImageCompressionLib.processing_modules

import ImageCompressionLib.constants.State
import ImageCompressionLib.containers.matrix.ShortMatrix
import ImageCompressionLib.containers.Parameters
import ImageCompressionLib.containers.TripleShortMatrix
import ImageCompressionLib.containers.type.Flag
import ImageCompressionLib.containers.type.Size
import ImageCompressionLib.utils.objects.TimeManager
import utils.BuffImConvertor
import org.junit.Test

import java.awt.image.BufferedImage
import java.util.Random

import org.junit.Assert.*
import org.junit.Before

class ModuleImageTest {
    @Before
    fun befor(){
//        param = Flag()
    }

    @Test
    fun TestDefaultRaundomBufIm5() {
        var bufferedImage= randomBufferedImage(122,124)
        val param=Parameters.createParametresForTest(Size(122,124))
        param.flag.setFalse(Flag.Parameter.Enlargement)
        val myBufferedImage=BuffImConvertor.instance.convert(bufferedImage)

        var w=myBufferedImage.width
        var h=myBufferedImage.height

        val module = ModuleImage(myBufferedImage, param)
        val matrix = module.getTripleShortMatrix(true)

        val module1 = ModuleImage(matrix)
        val myBufferedImage1 = module1.getBufferedImage(true)

        val arr1 = myBufferedImage.getIntArray()//?.getRGB(0, 0, w, h, null, 0, w)
        val arr2 = myBufferedImage1.getIntArray()//.getRGB(0, 0, w, h, null, 0, w)

        assertArrayInRange(arr1, arr2, 5)
    }
    @Test
    fun TestDefaultGradientMatrix5() {
        var bufferedImage= getGradientMatrix(122,124)
        val mbi=ModuleImage(bufferedImage).getBufferedImage(true)

        val module = ModuleImage(mbi,bufferedImage.parameters)
        val matrix = module.getTripleShortMatrix(true)

        val module1 = ModuleImage(matrix)
        val myBufferedImage1 = module1.getBufferedImage(true)

        val arr=myBufferedImage1.getIntArray()
        val arr1=mbi.getIntArray()

        assertArrayInRange(arr1,arr,5)
    }
    @Test
    fun TestDefaultGradientMatrixRGB5() {
        var bufferedImage= getGradientMatrix(122,124)
        val cpy=bufferedImage.copy()

        val module = ModuleImage(bufferedImage)
        val matrix = module.getTripleShortMatrix(true)

        val module1 = ModuleImage(matrix)
//        val rgbMatrix = module1.getRgbMatrixOld()
//
//        cpy.assertMatrixInRange(rgbMatrix,5)

    }

/*
    @Test
    fun TestEnlagment4() {
        val grad=getGradientMatrix(124,546)
        val param=Parameters.createParametresForTest(Size(124,546))
        param.flag.setChecked(Flag.Parameter.Enlargement,true)
//        param.imageSize= Size(124,546)

        val mi= ModuleImage(grad)
        val bmp=mi.getRgbMatrixOld().copy()
        var enl=mi.getYenlMatrix(true)
        assertFails { assertTrue(bmp.assertMatrixInRange(enl,1)) }

        val mi1= ModuleImage(enl)
        val bmp1=mi1.getRgbMatrixOld()
        kotlin.test.assertTrue { bmp1.assertMatrixInRange(bmp,4) }

    }
*/

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
        val param=Parameters.createParametresForTest(Size(w,h))

        TimeManager.Instance.startNewTrack("m BmpToYenl ${loop}l ${w}x$h")
        for(i in 0..loop-1) {
            val myBufferedImage = ModuleImage(mBI, param)
            val matrix = myBufferedImage.getTripleShortMatrix(true)
        }
        TimeManager.Instance.append("multi")
        println(TimeManager.Instance.getInfoInSec())
        val t1=TimeManager.Instance.getTotalTime()
        TimeManager.Instance.startNewTrack("o BmpToYenl ${loop}l ${w}x$h")
        for(i in 0..loop-1) {
            val myBufferedImage = ModuleImage(mBI, param)
            val matrix = myBufferedImage.getTripleShortMatrix(false)
        }
        TimeManager.Instance.append("one")
        println(TimeManager.Instance.getInfoInSec())
        val t2=TimeManager.Instance.getTotalTime()
        println("multi $t1;one $t2")
        assertTrue(t2>t1)

    }
    fun getGradientMatrix(w:Int,h:Int): TripleShortMatrix {
        val a=ShortMatrix(w,h){i, j -> ((i+j)%255).toShort() }
        val b=ShortMatrix(w,h){i, j -> ((i+j)%255).toShort() }
        val c=ShortMatrix(w,h){i, j -> ((i+j)%255).toShort() }
        return TripleShortMatrix(a,b,c,Parameters.createParametresForTest(Size(w,h)),State.Origin)

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