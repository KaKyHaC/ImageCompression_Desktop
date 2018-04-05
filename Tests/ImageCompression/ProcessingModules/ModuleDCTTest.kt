package ImageCompression.ProcessingModules

import ImageCompression.Containers.Flag
import ImageCompression.Utils.Functions.ImageIOTest
import ImageCompression.Utils.Objects.TimeManager
import org.junit.Assert.*
import org.junit.Test
import java.util.*

class ModuleDCTTest{
    @Test
    fun DerRevDCTTest() {
        val m = ImageIOTest.createMatrix(1920, 1080)
        val mi = MyBufferedImage(m, Flag())
        val enl = mi.getYenlMatrix(true)
        val cpy=enl.copy()
        assertEquals(cpy,enl)

        val dctModule = ModuleDCT(enl,Flag())

        dctModule.getDCTMatrix(true);
        assertFalse( cpy.assertMatrixInRange(enl,8))

        dctModule.getYCbCrMatrix(true)
        assertTrue(cpy.assertMatrixInRange(enl,8))

        dctModule.getDCTMatrix(false)
        assertFalse( cpy.assertMatrixInRange(enl,8))

        dctModule.getYCbCrMatrix(false)
        assertTrue(cpy.assertMatrixInRange(enl,8))
    }
    @Test
    fun TimeTest1(){
        TimeTest(1920,1080,1)
    }
    @Test
    fun TimeTest2(){
        TimeTest(360,240,1)
    }
    @Test
    fun TimeTest3(){
        TimeTest(1920,1080,3)
    }
    @Test
    fun TimeTest4(){
        TimeTest(360,240,3)
    }
    @Test
    fun GlobalTest1(){
        GlobalTest(360,240,1,8)
    }
    @Test
    fun GlobalTest2(){
        GlobalTest(360,240,3,24)
    }
    @Test
    fun GlobalTest3(){
        GlobalTest(1920,1080,1,8)
    }
    @Test
    fun GlobalTest4(){
        GlobalTest(1920,1080,3,24)
    }

    fun TimeTest(w:Int ,h:Int,loop:Int){
        val m= ImageIOTest.createMatrix(w,h)
        val mi=MyBufferedImage(m,Flag())
        val enl=mi.getYenlMatrix(true)
        val cpy=enl.copy()

        val dctModule=ModuleDCT(enl,Flag())

        val t1= Date().time
        for (i in 0..loop-1){
            dctModule.getDCTMatrix(false)
            dctModule.getYCbCrMatrix(false)
        }
        val t2= Date().time

        assertTrue("first",enl.assertMatrixInRange(cpy,10*loop))

//        println("start Async")
        val t3= Date().time
        for (i in 0..loop-1){
            dctModule.getDCTMatrix(true)
            dctModule.getYCbCrMatrix(true)
        }
        val t4= Date().time

        assertTrue("second",enl.assertMatrixInRange(cpy,10*loop))


        System.out.println("dir/rev DCT${loop} ${w}x$h: one=${t2-t1}, multi=${t4-t3}")
        assertTrue("(${t2-t1})>(${t4-t3})",(t2-t1)>(t4-t3))
    }
    fun GlobalTest(w:Int,h:Int,loop:Int,dif:Int){
        val m = ImageIOTest.createMatrix(w, h)
        val mi = MyBufferedImage(m,Flag())
        val enl = mi.getYenlMatrix(true)
        val cpy=enl.copy()
        assertEquals(cpy,enl)

        val dctModule = ModuleDCT(enl,Flag())

        TimeManager.Instance.startNewTrack("mDCT ${loop}l,${dif}dif (${w}x$h)")
        for(i in 0..loop-1) {
            dctModule.getDCTMatrix(true)
            assertFalse(cpy.assertMatrixInRange(enl, dif))

            dctModule.getYCbCrMatrix(true)
            assertTrue(cpy.assertMatrixInRange(enl, dif))
        }
        TimeManager.Instance.append("multiThread DCT")

        for (i in 0..loop-1) {
            dctModule.getDCTMatrix(false)
            assertFalse(cpy.assertMatrixInRange(enl, dif))


            dctModule.getYCbCrMatrix(true)
            assertTrue(cpy.assertMatrixInRange(enl, dif))
        }
        TimeManager.Instance.append("one Thread DCT")
        println(TimeManager.Instance.getInfoInSec())
        assertTrue("TimeFaild",TimeManager.Instance.getLineSegment(0)<TimeManager.Instance.getLineSegment(1))
    }
}