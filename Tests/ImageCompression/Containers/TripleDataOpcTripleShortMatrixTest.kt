package ImageCompression.Containers

import ImageCompression.Constants.SIZEOFBLOCK
import org.junit.Assert.*
import org.junit.Test
import java.math.BigInteger
import java.util.*
import kotlin.test.assertFails

class TripleDataOpcTripleShortMatrixTest {
    val box= TripleDataOpcMatrix()
    val box1= TripleDataOpcMatrix()
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

    fun init(boxMatrixData: TripleDataOpcMatrix){
        flag.setChecked(Flag.Parameter.OneFile,true)
        flag.setChecked(Flag.Parameter.DC,true)
        flag.setChecked(Flag.Parameter.LongCode,true)
        val size=200

        var a=Array(size,{x->Array(size,{y->initDopc(DataOpc(),flag)})})
        boxMatrixData.a=a
        a=Array(size,{x->Array(size,{y->initDopc(DataOpc(),flag)})})
        boxMatrixData.b=a
        a=Array(size,{x->Array(size,{y->initDopc(DataOpc(),flag)})})
        boxMatrixData.c=a
    }
    fun initDopc(DataOpc: DataOpc, flag: Flag):DataOpc{
        val size=5
        if(flag.isChecked(Flag.Parameter.LongCode))
            for(i in 0..size)
                DataOpc.vectorCode.addElement(rand.nextLong())
        else
            DataOpc.N= BigInteger(ByteArray(size,{ x->x.toByte()}))

        if(flag.isChecked(Flag.Parameter.DC))
            DataOpc.DC=rand.nextInt().toShort()

        if(flag.isChecked(Flag.Parameter.OneFile)&&!flag.isChecked(Flag.Parameter.GlobalBase))
            forEach(SIZEOFBLOCK, SIZEOFBLOCK,{ x, y -> DataOpc.base[x]=rand.nextInt(0xff).toShort() })

        forEach(SIZEOFBLOCK, SIZEOFBLOCK,{ x, y -> DataOpc.sign[x][y]=rand.nextBoolean() })

        return DataOpc
    }
    fun forEach(w:Int,h:Int,let:(x:Int,y:Int)->Unit){
        for(i in 0..w-1){
            for(j in 0..h-1){
                let.invoke(i,j)
            }
        }
    }
}