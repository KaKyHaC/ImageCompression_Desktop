package ImageCompression.Utils.Objects

import ImageCompression.Utils.Objects.DataOPC.SIZEOFBLOCK
import org.junit.Before

import org.junit.Assert.*
import org.junit.Test
import java.math.BigInteger
import java.util.*
import javax.print.attribute.IntegerSyntax
import kotlin.test.assertFails

class DataOPCTest {
    var dopc=DataOPC()
    var dopc1=DataOPC()
    val rand=Random()

    @Test
    fun TestParsers(){
        initDopc(dopc)
        dopc1=dopc.copy()
        AssertDataOpcEqual(dopc1,dopc)

        val bs=dopc.BinaryStringGet()
        val base=dopc.FromBaseToArray()
        val sing=dopc.SignToString()

        dopc.BinaryStringSet(kotlin.ByteArray(SIZEOFBLOCK,{x->0}))
        dopc.FromArrayToBase(kotlin.ShortArray(SIZEOFBLOCK,{x->0}))
        dopc.SingFromString(kotlin.ByteArray(SIZEOFBLOCK,{x->0}))
        assertFails { AssertDataOpcEqual(dopc,dopc1) }

        dopc.BinaryStringSet(bs)
        dopc.FromArrayToBase(base)
        dopc.SingFromString(sing)
        AssertDataOpcEqual(dopc,dopc1)
    }
    @Test
    fun TestToString(){
        val a:Short = 123
        val s=a.toString()
        val c=a.toChar()
        val s2=c.toString()
        val s1= String(kotlin.CharArray(1,{x->c}))
        val i=123
        val s3=i.toString()
        val s4=i.toChar().toString()
        val i1=12345678
        val s5=i1.toString()
        val s6=i1.toChar().toString()
        val s7=i1.toString(16)
        val s8=i1.toString(32)
        val s9=StringBuilder()
        s9.append(i1)
//        val s10=i1.toString(64)
    }

    @Test
    fun TestAssertFun(){
        var dopc=DataOPC()
        var dopc1=DataOPC()
        AssertDataOpcEqual(dopc,dopc1)

        initDopc(dopc)
        initDopc(dopc1)
        AssertDataOpcEqual(dopc,dopc)
        assertFails { AssertDataOpcEqual(dopc,dopc1) }

    }
    @Test
    fun TestCOPY(){
        initDopc(dopc)
        assertFails { AssertDataOpcEqual(dopc,dopc1) }

        dopc1=dopc.copy()
        AssertDataOpcEqual(dopc,dopc1)

        initDopc(dopc1)
        assertFails { AssertDataOpcEqual(dopc,dopc1) }
    }

    fun DataOPC.copy():DataOPC{
        var res=DataOPC()
        res.N= BigInteger(N.toByteArray())
        res.DC=DC
        for(i in 0..SIZEOFBLOCK-1) {
            res.base[i]=base[i]
            for(j in 0..SIZEOFBLOCK-1)
                res.sign[i][j]=sign[i][j]
        }
        for (i in 0..Code.size-1)
            res.Code.addElement(Code[i])

        return res
    }

    fun AssertDataOpcEqual(a:DataOPC,b:DataOPC){
        for(i in 0..a.base.size-1)
            assertEquals("at $i base ${a.base[i]}!=${b.base[i]}",a.base[i],b.base[i])

        assertEquals("DC ${a.DC}!=${b.DC}",a.DC,b.DC)

        forEach(SIZEOFBLOCK, SIZEOFBLOCK,
                {x, y -> assertEquals("sign at[$x][$y] ${a.sign[x][y]}!=${b.sign[x][y]} "
                ,a.sign[x][y],b.sign[x][y]) })

        for(i in 0..a.Code.size-1)
            assertEquals("at $i code ${a.Code[i]}!=${b.Code[i]}",a.Code[i],b.Code[i])

        assertEquals("BI ${a.N}!=${b.N}",a.N,b.N)
    }
    fun initDopc(dataOPC: DataOPC){
        val size=5
        for(i in 0..size)
            dataOPC.Code.addElement(rand.nextLong())

        dataOPC.DC=rand.nextInt().toShort()
        dataOPC.N= BigInteger(kotlin.ByteArray(size,{x->x.toByte()}))
        forEach(DataOPC.SIZEOFBLOCK,DataOPC.SIZEOFBLOCK,{x, y -> dataOPC.base[x]=rand.nextInt().toShort() })
        forEach(DataOPC.SIZEOFBLOCK,DataOPC.SIZEOFBLOCK,{x, y -> dataOPC.sign[x][y]=rand.nextBoolean() })

    }
    fun forEach(w:Int,h:Int,let:(x:Int,y:Int)->Unit){
        for(i in 0..w-1){
            for(j in 0..h-1){
                let.invoke(i,j)
            }
        }
    }
}