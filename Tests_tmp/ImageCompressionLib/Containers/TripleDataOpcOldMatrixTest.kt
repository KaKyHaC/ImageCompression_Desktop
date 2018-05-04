package ImageCompressionLib.Containers

import ImageCompressionLib.Constants.SIZEOFBLOCK
import ImageCompressionLib.Containers.Type.ByteVector
import ImageCompressionLib.Containers.Type.Flag
import ImageCompressionLib.Containers.Type.Size
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import java.math.BigInteger

class TripleDataOpcOldMatrixTest {
    val dopc= DataOpcOld()
    val flag= Flag()
    @Before
    fun init(){
//        dopc.DC=200
        dopc.N= BigInteger.valueOf(12312312412412124)
        dopc.base= ShortArray(SIZEOFBLOCK){n->255 }
        dopc.FromArrayToSing(ByteArray(SIZEOFBLOCK){n->n.toByte()})

        flag.setChecked(Flag.Parameter.GlobalBase,true)
        flag.setChecked(Flag.Parameter.OneFile,true)
        flag.setTrue(Flag.Parameter.DCT)
    }


    @Test
    fun testEquals(){
        val size=9
        val arr=Array(size){Array(size){dopc}}
        val trip1=TripleDataOpcMatrix(arr,arr,arr)
        val trip2=TripleDataOpcMatrix(arr,arr,arr)

        assertEquals(trip1,trip1)
        assertEquals(trip1,trip2)
    }
    @Test
    fun testGlobalBase1(){
        IOGloalBase(Size(1, 1), Size(1, 1))
    }
    @Test
    fun testGlobalBase4_1(){
        IOGloalBase(Size(4, 4), Size(1, 1))
    }
    @Test
    fun testGlobalBase4_2(){
        IOGloalBase(Size(4, 4), Size(2, 2))
    }
    @Test
    fun testGlobalBase400_20(){
        IOGloalBase(Size(400, 400), Size(20, 20))
    }
    @Test
    fun testGlobalBase4_3(){
        IOGloalBase(Size(4, 4), Size(3, 3))
    }
    fun IOGloalBase(size: Size, globalBase: Size) {
        val arr=Array(size.width){Array(size.height){dopc}}
        val trip1=TripleDataOpcMatrix(arr,arr,arr)

        val vec= ByteVector()
        trip1.writeBaseToVector(vec,flag,globalBase.width,globalBase.height)
        trip1.writeToVector(vec,flag)

        val trip2=TripleDataOpcMatrix()
        trip2.readBaseFromVector(vec,flag)
        trip2.readFromVector(vec,flag)

        assertEquals(trip1,trip2)
    }
}