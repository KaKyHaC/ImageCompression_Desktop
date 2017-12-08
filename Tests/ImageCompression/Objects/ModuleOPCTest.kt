package ImageCompression.Objects

import ImageCompression.Constants.State
import ImageCompression.Containers.MatrixTest
import ImageCompression.Utils.Functions.ImageIOTest
import ImageCompression.Utils.Objects.TimeManager
import org.junit.Assert.*
import org.junit.Test
import java.util.*

class ModuleOPCTest{
    @Test
    fun TestDirectTime(){
        val m=ImageIOTest.createMatrix(1080,1920)
        m.state=State.DCT
        val opcModule=ModuleOPC(m)

        val loop=1
        val t1=Date().time
        for (i in 0..loop){
            opcModule.directOPC()
        }
        val t2=Date().time

        val t3=Date().time
        for (i in 0..loop){
            opcModule.directOPCMultiThreads()
        }
        val t4=Date().time

        System.out.println("directOPC ${loop}: one=${t2-t1}, multi=${t4-t3}")
        assertTrue((t2-t1)>(t4-t3))
    }
    @Test
    fun TestReverseTime(){
        val m=ImageIOTest.createMatrix(1080,1920)
        m.state=State.DCT
        val opcModule=ModuleOPC(m)
        val box=opcModule.getBoxOfOpc(true)

        val loop=1
        val t1=Date().time
        for (i in 0..loop){
            opcModule.reverseOPC()
        }
        val t2=Date().time
//        println("Start async")
        val t3=Date().time
        for (i in 0..loop){
            opcModule.reverseOPCMultiThreads()
        }
        val t4=Date().time

        System.out.println("reverseOPC ${loop}: one=${t2-t1}, multi=${t4-t3}")
        assertTrue((t2-t1)>(t4-t3))
    }
    @Test
    fun GlobalTest1(){//477 sec
        GlobalTest(360,240,1)
    }
    @Test
    fun GlobalTest2(){//1386 sec
        GlobalTest(360,240,3)
    }
    @Test
    fun GlobalTest3(){//11343 sec
        GlobalTest(1920,1080,1)
    }
    @Test
    fun GlobalTest4(){//34478 sec
        GlobalTest(1920,1080,3)
    }
    fun GlobalTest(w:Int,h:Int,loop:Int){
        val m=ImageIOTest.createMatrix(w,h)
        m.state=State.DCT
        val opcModule=ModuleOPC(m)
        val tm=TimeManager.Instance

        val cpy=m.copy()
        assertEquals(m,cpy)

        tm.startNewTrack("mOPC(${w}x$h),${loop}l")
        for(i in 0..loop-1){
            opcModule.directOPC()
            val resModule = ModuleOPC(opcModule.getBoxOfOpc(false),opcModule.flag)
            resModule.reverseOPC()
            val res=resModule.getMatrix(false)
            assertEquals(res,cpy)
        }
        tm.append("one")

        for(i in 0..loop-1){
            opcModule.directOPCMultiThreads()
            val resModule = ModuleOPC(opcModule.getBoxOfOpc(true),opcModule.flag)
            resModule.reverseOPCMultiThreads()
            val res=resModule.getMatrix(true)
            assertEquals(res,cpy)
        }
        tm.append("multi")

        println(tm.getInfoInSec())
        assertTrue(tm.getLineSegment(0)>tm.getLineSegment(1))
    }
}