package ImageCompressionLib.ProcessingModules

import ImageCompressionLib.Utils.Functions.ImageIOTest
import ImageCompressionLib.Utils.Objects.TimeManager
import org.junit.Assert.*
import org.junit.Test
import java.util.*
import kotlin.test.assertFails

class ModuleDCTTest{
    val range=8
    @Test
    fun DerRevDCTTest() {
        val m = ImageIOTest.createMatrix(1920, 1080)
        val mi = ModuleImage(m)
        val enl = mi.getTripleShortMatrix(true)
        val cpy=enl.copy()
        assertEquals(cpy,enl)

        val dctModule = ModuleDCT(enl)

        val dct=dctModule.getDCTMatrix(true);
        assertFails {( cpy.assertMatrixInRange(dct,range))}

        val ynl=dctModule.getYCbCrMatrix(true)
        assertTrue(cpy.assertMatrixInRange(ynl,range))

        val dct1=dctModule.getDCTMatrix(false)
        assertFails { ( cpy.assertMatrixInRange(dct1,range*2))}

        val ybr1=dctModule.getYCbCrMatrix(false)
        assertTrue(cpy.assertMatrixInRange(ybr1,range*2))
    }
//    @Test
//    fun TimeTest1(){
//        TimeTest(1920,1080,1)
//    }
//    @Test
//    fun TimeTest2(){
//        TimeTest(360,240,1)
//    }
//    @Test
//    fun TimeTest3(){
//        TimeTest(1920,1080,3)
//    }
//    @Test
//    fun TimeTest4(){
//        TimeTest(360,240,3)
//    }
    @Test
    fun GlobalTest360x240(){
        GlobalTest(360,240,1,8)
    }
    @Test
    fun GlobalTest360x240x3(){
        GlobalTest(360,240,3,24)
    }
    @Test
    fun GlobalTestHD(){
        GlobalTest(1920,1080,1,8)
    }
    @Test
    fun GlobalTestHDx3(){
        GlobalTest(1920,1080,3,24)
    }
    @Test
    fun GlobalTest13x25(){
        GlobalTest(13,25,1,8)
    }

    fun TimeTest(w:Int ,h:Int,loop:Int){
        val m= ImageIOTest.createMatrix(w,h)
        val mi= ModuleImage(m)
        val enl=mi.getTripleShortMatrix(true)
        val cpy=enl.copy()

        val dctModule=ModuleDCT(enl)

        val t1= Date().time
        for (i in 0..loop-1){
            dctModule.getDCTMatrix(false)
            dctModule.getYCbCrMatrix(false)
        }
        val t2= Date().time

        assertTrue("first",enl.assertMatrixInRange(cpy,10+loop))

//        println("start Async")
        val t3= Date().time
        for (i in 0..loop-1){
            dctModule.getDCTMatrix(true)
            dctModule.getYCbCrMatrix(true)
        }
        val t4= Date().time

        assertTrue("second",enl.assertMatrixInRange(cpy,10+loop))


        System.out.println("dir/rev DCT${loop} ${w}x$h: one=${t2-t1}, multi=${t4-t3}")
        assertTrue("(${t2-t1})>(${t4-t3})",(t2-t1)>(t4-t3))
    }
    fun GlobalTest(w:Int,h:Int,loop:Int,dif:Int){
        val m = ImageIOTest.createMatrix(w, h)
        val mi = ModuleImage(m)
        val enl = mi.getTripleShortMatrix(true)
        val cpy=enl.copy()
        assertEquals(cpy,enl)

        val dctModule = ModuleDCT(enl)

        TimeManager.Instance.startNewTrack("mDCT ${loop}l,${dif}dif (${w}x$h)")
        for(i in 0..loop-1) {
            var enl=dctModule.getDCTMatrix(true)
            assertFails { (cpy.assertMatrixInRange(enl, dif))}

            enl=dctModule.getYCbCrMatrix(true)
            assertTrue(cpy.assertMatrixInRange(enl, dif))
        }
        TimeManager.Instance.append("multiThread DCT")

        for (i in 0..loop-1) {
            var enl=dctModule.getDCTMatrix(false)
            assertFails { (cpy.assertMatrixInRange(enl, dif))}

            enl=dctModule.getYCbCrMatrix(false)
            assertTrue(cpy.assertMatrixInRange(enl, dif))
        }
        TimeManager.Instance.append("one Thread DCT")
        println(TimeManager.Instance.getInfoInSec())
        assertTrue("TimeFaild",TimeManager.Instance.getLineSegment(0)<TimeManager.Instance.getLineSegment(1))
    }
}