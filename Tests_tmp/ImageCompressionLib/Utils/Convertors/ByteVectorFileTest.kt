//package ImageCompressionLib.Utils.Convertors
//
//import ImageCompressionLib.Data.Primitives.ByteVector
//import ImageCompressionLib.Data.Type.Flag
//import org.junit.Assert.*
//import org.junit.Test
//import java.util.*
//
//class ByteVectorFileTest{
//    val path="/files/test1.txt"
//    val size=200000
//    val rand=Random()
//    @Test
//    fun TotalTest(){
//        val b=getRandomVector(size)
//        val f= Flag(0xfff)
//        f.setChecked(Flag.Parameter.OneFile,true)
////        f.setChecked(Flag.Parameter.LongCode,true)
////        f.setChecked(Flag.Parameter.DC,true)
//
//        val fw=ByteVectorFile(path)
//        fw.write(b,f)
//
//        val fr=ByteVectorFile(path)
//        val r=fr.read()
//        val rb=r.first
//        val rf=r.second
//
//        assertEquals("falg",f,rf)
//        assertEquals("vector",b,rb)
//        System.out.println(fr.infoToString())
//    }
//    fun getRandomVector(size:Int): ByteVector {
//        var bv= ByteVector(size)
//        for (i in 0..size-1){
//            bv.append(rand.nextInt().toByte())
//        }
//        return bv
//    }
//}