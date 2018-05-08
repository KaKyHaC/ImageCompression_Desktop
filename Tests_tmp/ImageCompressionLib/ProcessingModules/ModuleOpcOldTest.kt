package ImageCompressionLib.ProcessingModules

import ImageCompressionLib.Constants.State
import ImageCompressionLib.Containers.Parameters
import ImageCompressionLib.Containers.Type.Size
import ImageCompressionLib.Utils.Functions.ImageIOTest
import ImageCompressionLib.Utils.Objects.TimeManager
import org.junit.Assert.*
import org.junit.Test

class ModuleOpcTest {
    /*@Test
    fun TestDirectTime(){
        val m=ImageIOTest.createMatrix(1080,1920)
        m.state=State.DCT
        val opcModule= ModuleOpc(m, Parameters.createParametresForTest())

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
        val opcModule= ModuleOpc(m, Flag(), true)
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
    }*/
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
        val opcModule= ModuleOpc(m)
        val tm=TimeManager.Instance

        val cpy=m.copy()
        assertEquals(m,cpy)

        tm.startNewTrack("ModuleOPC")
        for(i in 0 until loop) {
            val tdom = opcModule.getTripleDataOpcMatrix(null)
            val m2 = ModuleOpc(tdom)
            val res = m2.getTripleShortMatrix(null).first
            tm.append("end")
            assertEquals(res, cpy)
        }
        println(tm.getInfoInSec())
    }
}