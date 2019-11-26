/*
package ImageCompressionLib.Containers

import ImageCompressionLib.Constants.SIZE_OF_BLOCK
import ImageCompressionLib.Containers.Type.ByteVector
import ImageCompressionLib.Containers.Type.Flag
import org.junit.Assert.*
import org.junit.Test
import java.math.BigInteger
import java.util.*
import kotlin.test.assertFails

class TripleDataOpcOldTripleShortMatrixOldTest {
    val box= TripleDataOpcMatrixOld()
    val box1= TripleDataOpcMatrixOld()
    val flag= Flag("0")
    var rand=Random()

    @Test
    fun TestEqual(){
        assertEquals(box,box)
        assertEquals(box,box1)

        init(box)
        assertFails {assertEquals(box,box1)}
    }
    @Test
    fun TestCopy(){
        init(box)
        assertFails { assertEquals(box,box1) }

        val box1=box.copy()
        assertEquals(box,box1)
    }
    @Test
    fun TestVector(){
        init(box)
        var bv= ByteVector()

        box.writeToVector(bv,flag)
        assertFails { assertEquals(box,box1) }

        box1.readFromVector(bv,flag)
        assertEquals(box,box1)
    }

    fun init(boxMatrixDataOld: TripleDataOpcMatrixOld){
        flag.setChecked(Flag.Parameter.OneFile,true)
        flag.setChecked(Flag.Parameter.DC,true)
        flag.setChecked(Flag.Parameter.LongCode,true)
        val size=200

        var a=Array(size,{x->Array(size,{y->initDopc(DataOpcOld(),flag)})})
        boxMatrixDataOld.a=a
        a=Array(size,{x->Array(size,{y->initDopc(DataOpcOld(),flag)})})
        boxMatrixDataOld.b=a
        a=Array(size,{x->Array(size,{y->initDopc(DataOpcOld(),flag)})})
        boxMatrixDataOld.c=a
    }
    fun initDopc(DataOpcOld: DataOpcOld, flag: Flag): DataOpcOld {
        val size=5
        if(flag.isChecked(Flag.Parameter.LongCode))
            for(i in 0..size)
                DataOpcOld.vectorCode.addElement(rand.nextLong())
        else
            DataOpcOld.N= BigInteger(ByteArray(size,{ x->x.toByte()}))

        if(flag.isChecked(Flag.Parameter.DC))
            DataOpcOld.DC=rand.nextInt().toShort()

        if(flag.isChecked(Flag.Parameter.OneFile)&&!flag.isChecked(Flag.Parameter.GlobalBase))
            forEach(SIZE_OF_BLOCK, SIZE_OF_BLOCK,{ x, y -> DataOpcOld.base[x]=rand.nextInt(0xff).toShort() })

        forEach(SIZE_OF_BLOCK, SIZE_OF_BLOCK,{ x, y -> DataOpcOld.sign[x][y]=rand.nextBoolean() })

        return DataOpcOld
    }
    fun forEach(w:Int,h:Int,let:(x:Int,y:Int)->Unit){
        for(i in 0..w-1){
            for(j in 0..h-1){
                let.invoke(i,j)
            }
        }
    }
}*/
