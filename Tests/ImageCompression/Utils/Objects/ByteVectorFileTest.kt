package ImageCompression.Utils.Objects

import org.junit.Assert.*
import org.junit.Test
import java.util.*

class ByteVectorFileTest{
    val path="/files/test1.txt"
    val size=200000
    val rand=Random()
    @Test
    fun TotalTest(){
        val b=getRandomVector(size)
        val f=Flag("0")
        f.isDC=true
        f.isOneFile=true

        val fw=ByteVectorFile(path)
        fw.write(b,f)

        val fr=ByteVectorFile(path)
        val r=fr.read()
        val rb=r.first
        val rf=r.second

        assertEquals(f.flag,rf.flag)
        assertEquals(b,rb)
        System.out.println(fr.infoToString())
    }
    fun getRandomVector(size:Int):ByteVector{
        var bv=ByteVector(size)
        for (i in 0..size-1){
            bv.append(rand.nextInt().toByte())
        }
        return bv
    }
}