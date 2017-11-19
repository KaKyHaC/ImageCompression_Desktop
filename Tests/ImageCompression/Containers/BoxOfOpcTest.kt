package ImageCompression.Containers

import ImageCompression.Utils.Objects.DataOPC
import ImageCompression.Utils.Objects.DataOPC.SIZEOFBLOCK
import ImageCompression.Utils.Objects.Flag
import org.junit.Assert.*
import org.junit.Test
import java.math.BigInteger
import java.util.*
import kotlin.test.assertFails

class BoxOfOpcTest{
    val box=BoxOfOpc()
    val box1=BoxOfOpc()
    val flag=Flag("0")
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

    fun init(box:BoxOfOpc){
        flag.isOneFile=true
        flag.isDC=true
        flag.isLongCode=true
        val size=200

        var a=Array(size,{x->Array(size,{y->initDopc(DataOPC(),flag)})})
        box.a=a
        a=Array(size,{x->Array(size,{y->initDopc(DataOPC(),flag)})})
        box.b=a
        a=Array(size,{x->Array(size,{y->initDopc(DataOPC(),flag)})})
        box.c=a
    }
    fun initDopc(dataOPC: DataOPC, flag: Flag):DataOPC{
        val size=5
        if(flag.isLongCode)
            for(i in 0..size)
                dataOPC.Code.addElement(rand.nextLong())
        else
            dataOPC.N= BigInteger(ByteArray(size,{ x->x.toByte()}))

        if(flag.isDC)
            dataOPC.DC=rand.nextInt().toShort()

        if(flag.isOneFile&&!flag.isGlobalBase)
            forEach(SIZEOFBLOCK, SIZEOFBLOCK,{ x, y -> dataOPC.base[x]=rand.nextInt(0xff).toShort() })

        forEach(SIZEOFBLOCK, SIZEOFBLOCK,{ x, y -> dataOPC.sign[x][y]=rand.nextBoolean() })

        return dataOPC
    }
    fun forEach(w:Int,h:Int,let:(x:Int,y:Int)->Unit){
        for(i in 0..w-1){
            for(j in 0..h-1){
                let.invoke(i,j)
            }
        }
    }
}