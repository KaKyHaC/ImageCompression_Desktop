package ImageCompressionLib.processing_modules

import ImageCompressionLib.constants.State
import ImageCompressionLib.containers.EncryptParameters
import ImageCompressionLib.containers.type.ByteVector
import ImageCompressionLib.containers.type.Size
import ImageCompressionLib.utils.functions.ImageIOTest
import ImageCompressionLib.utils.functions.opc.IStegoMessageUtil
import ImageCompressionLib.utils.objects.TimeManager
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.util.*

@RunWith(Parameterized::class)
class ModuleOpcTest(val w: Int,val h: Int,val loop: Int) {
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
    fun GlobalTest(){
        val m=ImageIOTest.createMatrix(w,h)
        m.state=State.Dct
        val opcModule= ModuleOpc(m)
        val tm=TimeManager.Instance

        val cpy=m.copy()
        assertEquals(m,cpy)

        tm.startNewTrack("ModuleOPC")
        for(i in 0 until loop) {
            tm.append("start")
            val tdom = opcModule.getTripleDataOpcMatrix(null)
            val m2 = ModuleOpc(tdom)
            val res = m2.getTripleShortMatrix(null).first
            tm.append("end")
            assertEquals(res, cpy)
        }
        println(tm.getInfoInSec())
    }
    @Test
    fun GlobalTestUnitSize7x6() {
        val m = ImageIOTest.createMatrix(w, h,Size(7, 6))
//        m.parameters.unitSize = Size(7, 6)
        m.state = State.Dct
        val cpy = m.copy()

        val opcModule = ModuleOpc(m)
        val tdom = opcModule.getTripleDataOpcMatrix(null)
        val m2 = ModuleOpc(tdom)
        val res = m2.getTripleShortMatrix(null).first

        assertEquals(res, cpy)
    }
    @Test
    fun StegoAtZero(){
        TestSteganographyAtPos(0)
    }
    @Test
    fun StegoAtFirst(){
        TestSteganographyAtPos(1)
    }
    fun TestSteganographyAtPos(pos:Int) {
        val m = ImageIOTest.createMatrix(w, h,Size(1,8))
        m.state = State.Dct
        val cpy = m.copy()
        val eP=EncryptParameters()
        eP.message= ByteVector(w/2)
        for(i in 0 until eP.message!!.maxSize)
            eP.message!!.append(Random().nextLong().toShort())
        val messCpy=eP.message!!.copy()
        eP.steganography=EncryptParameters.StegaParameters(pos){object :IStegoMessageUtil{
            override fun isUseNextBlock(): Boolean {
                return true
            }
        }}


        val opcModule = ModuleOpc(m)
        val tdom = opcModule.getTripleDataOpcMatrix(eP)
        val m2 = ModuleOpc(tdom)
        val res = m2.getTripleShortMatrix(eP)
        val mess=res.second
        val mat=res.first

        assertEquals(messCpy,mess!!)
//        cpy.assertMatrixInRange(mat,2)
    }
    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "w:{0},h:{1},loop:{2}")
        fun data(): Collection<Array<Any>> {
            return listOf(
                    arrayOf(36,24,1) as Array<Any>
                    ,arrayOf(13,25,1) as Array<Any>
                    ,arrayOf(360,240,1) as Array<Any>//477
                    ,arrayOf(360,240,3) as Array<Any>//1386
                    ,arrayOf(1920,1080,1) as Array<Any>//11343
                    ,arrayOf(1920,1080,3) as Array<Any>//34478
            )
        }
    }
}