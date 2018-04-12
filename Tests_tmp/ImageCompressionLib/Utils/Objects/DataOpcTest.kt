package ImageCompressionLib.Utils.Objects

import ImageCompressionLib.Constants.SIZEOFBLOCK
import ImageCompressionLib.Containers.ByteVector
import ImageCompressionLib.Containers.DataOpc
import ImageCompressionLib.Containers.Flag

import org.junit.Assert.*
import org.junit.Test
import java.io.DataOutputStream
import java.io.FileOutputStream
import java.math.BigInteger
import java.util.*
import kotlin.test.assertFails

class DataOpcTest {
    var dopc=DataOpc()
    var dopc1= DataOpc()
    val rand=Random()

    @Test
    fun TestBigInt(){
        val bi=BigInteger("939990213")
        val b=bi.toByteArray()
        val buf=ByteArray(b.size+1)
        System.arraycopy(b,0,buf,1,b.size)
        val rbi=BigInteger(buf)
        kotlin.test.assertTrue(bi.compareTo(rbi)==0)
    }
    @Test
    fun TestParsers(){
        initDopc(dopc)
        dopc1=dopc.copy()
        AssertDataOpcEqual(dopc1,dopc)

        val bs=dopc.FromBigIntToArray()
        val base=dopc.FromBaseToArray()
        val sing=dopc.FromSignToArray()

        dopc.FromArrayToBigInt(kotlin.ByteArray(SIZEOFBLOCK,{x->0}))
        dopc.FromArrayToBase(kotlin.ShortArray(SIZEOFBLOCK,{x->0}))
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
        var f= Flag("0")
        f.setChecked(Flag.Parameter.OneFile,true)
        f.setChecked(Flag.Parameter.LongCode,true)
        f.setChecked(Flag.Parameter.DC,true)
        dopc.N= BigInteger("0")
        val s=dopc.toString(f)
        dopc1.setFrom(s,f)
        AssertDataOpcEqual(dopc,dopc1)

        dopc.N= BigInteger("292304395025234324")
        dopc.vectorCode=DataOpc().vectorCode
        assertFails { AssertDataOpcEqual(dopc1,dopc) }

//        f.isLongvectorCode=false
//        val s1=dopc.toString(f)
//        dopc1.valueOf(s1,f)
//        AssertDataOpcEqual(dopc,dopc1)
    }
    @Test
    fun TestSignToVector(){
        initDopc(dopc)
        dopc1=dopc.copy()
        AssertDataOpcEqual(dopc,dopc1)

        val bv= ByteVector()
        dopc.FromSignToVector(bv)
        dopc1.FromVectorToSign(bv)
    }
    @Test
    fun TestByteVector(){
        var bv= ByteVector(33)
        var f= Flag("0")
        f.setChecked(Flag.Parameter.OneFile,true)
        f.setChecked(Flag.Parameter.LongCode,true)
        f.setChecked(Flag.Parameter.DC,true)
        initDopc(dopc,f)


        dopc.toByteVector(bv,f)
        dopc1.setFrom(bv,f)
        AssertDataOpcEqual(dopc1,dopc)

    }
    @Test
    fun TestEqualMethod(){
        initDopc(dopc)
        assertFails { assertEquals(dopc1,dopc) }
        dopc1=dopc.copy()
        assertEquals(dopc1,dopc)
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
        var dopc=DataOpc()
        var dopc1=DataOpc()
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

    fun DataOpc.copy():DataOpc{
        var res=DataOpc()
        res.N= BigInteger(N.toByteArray())
        res.DC=DC
        for(i in 0..SIZEOFBLOCK-1) {
            res.base[i]=base[i]
            for(j in 0..SIZEOFBLOCK-1)
                res.sign[i][j]=sign[i][j]
        }
        for (i in 0..vectorCode.size-1)
            res.vectorCode.addElement(vectorCode[i])

        return res
    }

    fun AssertDataOpcEqual(a:DataOpc,b:DataOpc){
        for(i in 0..a.base.size-1)
            assertEquals("at $i base ${a.base[i]}!=${b.base[i]}",a.base[i],b.base[i])

        assertEquals("DC ${a.DC}!=${b.DC}",a.DC,b.DC)

        forEach(SIZEOFBLOCK, SIZEOFBLOCK,
                {x, y -> assertEquals("sign at[$x][$y] ${a.sign[x][y]}!=${b.sign[x][y]} "
                ,a.sign[x][y],b.sign[x][y]) })

        for(i in 0..a.vectorCode.size-1)
            assertEquals("at $i vectorCode ${a.vectorCode[i]}!=${b.vectorCode[i]}",a.vectorCode[i],b.vectorCode[i])

        assertEquals("BI ${a.N}!=${b.N}",a.N,b.N)
    }
    fun initDopc(DataOpc: DataOpc){
        val size=5
        for(i in 0..size)
            DataOpc.vectorCode.addElement(rand.nextLong())

        DataOpc.DC=rand.nextInt().toShort()
        DataOpc.N= BigInteger(kotlin.ByteArray(size,{x->x.toByte()}))
        DataOpc.N= BigInteger("342352522332214")
        forEach(SIZEOFBLOCK,SIZEOFBLOCK,{x, y -> DataOpc.base[x]=rand.nextInt(0xff).toShort() })
        forEach(SIZEOFBLOCK,SIZEOFBLOCK,{x, y -> DataOpc.sign[x][y]=rand.nextBoolean() })

    }
    fun initDopc(DataOpc: DataOpc,flag: Flag){
        val size=5
        if(flag.isChecked(Flag.Parameter.LongCode))
            for(i in 0..size)
                DataOpc.vectorCode.addElement(rand.nextLong())
        else
            DataOpc.N= BigInteger(kotlin.ByteArray(size,{x->x.toByte()}))

        if(flag.isChecked(Flag.Parameter.DC))
            DataOpc.DC=rand.nextInt().toShort()

        if(flag.isChecked(Flag.Parameter.OneFile)&&!flag.isChecked(Flag.Parameter.GlobalBase))
            forEach(SIZEOFBLOCK,SIZEOFBLOCK,{x, y -> DataOpc.base[x]=rand.nextInt(0xff).toShort() })

        forEach(SIZEOFBLOCK,SIZEOFBLOCK,{x, y -> DataOpc.sign[x][y]=rand.nextBoolean() })

    }
    fun forEach(w:Int,h:Int,let:(x:Int,y:Int)->Unit){
        for(i in 0..w-1){
            for(j in 0..h-1){
                let.invoke(i,j)
            }
        }
    }
}