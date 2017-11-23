package ImageCompression.Objects

import ImageCompression.Constants.State
import ImageCompression.Containers.MatrixTest
import ImageCompression.Utils.Functions.ImageIOTest
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
        val box=opcModule.boxOfOpc

        val loop=1
        val t1=Date().time
        for (i in 0..loop){
            opcModule.reverseOPC()
        }
        val t2=Date().time

        val t3=Date().time
        for (i in 0..loop){
            opcModule.reverseOPCMultiThreads()
        }
        val t4=Date().time

        System.out.println("reverseOPC ${loop}: one=${t2-t1}, multi=${t4-t3}")
        assertTrue((t2-t1)>(t4-t3))
    }
}