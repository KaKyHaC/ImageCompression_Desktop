package ImageCompression.Objects

import ImageCompression.Constants.State
import ImageCompression.Utils.Functions.ImageIOTest
import org.junit.Assert.*
import org.junit.Test
import java.util.*

class ModuleDCTTest{
    @Test
    fun TimeTest(){
        val m= ImageIOTest.createMatrix(1080,1920)
        val mi=MyBufferedImage(m)
        val enl=mi.getYenlMatrix(true)
        val dctModule=ModuleDCT(enl)

        val loop=1
        val t1= Date().time
        for (i in 0..loop){
            dctModule.dataProcessing()
            dctModule.dataProcessing()
        }
        val t2= Date().time
//        println("start Async")
        val t3= Date().time
        for (i in 0..loop){
            dctModule.dataProcessingInThreads()
            dctModule.dataProcessingInThreads()
        }
        val t4= Date().time

        System.out.println("dir/rev DCT ${loop}: one=${t2-t1}, multi=${t4-t3}")
        assertTrue((t2-t1)>(t4-t3))
    }
}