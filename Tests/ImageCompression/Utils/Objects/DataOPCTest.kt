package ImageCompression.Utils.Objects

import ImageCompression.Utils.Objects.DataOPC.SIZEOFBLOCK
import org.junit.Before

import org.junit.Assert.*
import org.junit.Test
import java.io.DataOutputStream
import java.io.FileOutputStream
import java.math.BigInteger
import java.util.*
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

        val bs=dopc.FromBigIntToArray()
        val base=dopc.FromBaseToArray()
        val sing=dopc.FromSignToArray()

        dopc.FromArrayToBigInt(kotlin.ByteArray(SIZEOFBLOCK,{x->0}))
        dopc.FromArrayToBase(kotlin.ByteArray(SIZEOFBLOCK,{x->0}))
        dopc.FromArrayToSing(kotlin.ByteArray(SIZEOFBLOCK,{x->0}))
        assertFails { AssertDataOpcEqual(dopc,dopc1) }

        dopc.FromArrayToBigInt(bs)
        dopc.FromArrayToBase(base)
        dopc.FromArrayToSing(sing)
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
        val dos = DataOutputStream(FileOutputStream("fds"))
        var vb=Vector<Byte>()
        var ba=vb.toByteArray()
        dos.write(ba)
//        val s10=i1.toString(64)
    }
    @Test
    fun TestMyToString(){
        initDopc(dopc)
        var f=Flag("0")
        f.isOneFile=true
        f.isLongCode=true
        f.isDC=true
        dopc.N= BigInteger("0")
        val s=dopc.toString(f)
        dopc1.valueOf(s,f)
        AssertDataOpcEqual(dopc,dopc1)

        dopc.N= BigInteger("292304395025234324")
        dopc.Code=DataOPC().Code
        assertFails { AssertDataOpcEqual(dopc1,dopc) }

//        f.isLongCode=false
//        val s1=dopc.toString(f)
//        dopc1.valueOf(s1,f)
//        AssertDataOpcEqual(dopc,dopc1)
    }

    @Test
    fun TestLongToString(){
        var l=rand.nextLong()
        val cpy=l
        val s1=l.toString(32)
        var sb=StringBuilder()
        for(i in 0..3) {
            val c=l.toChar()
            val lc=c.toLong()
            sb.append(l.toChar())
            l = l shr 16
        }
        val s2=sb.toString()

        for(i in 3 downTo 0){
            l=l shl 16
            val cl=s2[i].toLong()
            l=l or s2[i].toLong()
        }
        assertEquals(l,cpy)
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
        dataOPC.N= BigInteger("342352522332214")
        forEach(DataOPC.SIZEOFBLOCK,DataOPC.SIZEOFBLOCK,{x, y -> dataOPC.base[x]=rand.nextInt(0xff).toShort() })
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