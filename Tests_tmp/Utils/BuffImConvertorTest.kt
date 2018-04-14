package Utils

import ImageCompressionLib.Containers.MyBufferedImage
import org.junit.Test

import org.junit.Assert.*

class BuffImConvertorTest {

    @Test
    fun convert() {
        tetsDirRev(20,20){i, j -> i+j }
        tetsDirRev(200,200){i, j ->  i+j}
    }
    @Test
    fun convertDiffSize() {
        tetsDirRev(240,20){i, j -> i+j }
        tetsDirRev(200,2400){i, j ->  i+j}
    }
    fun tetsDirRev(w:Int,h:Int,init:(i:Int,j:Int)->Int){
        val mbi=MyBufferedImage(w,h)
        mbi.forEach{ i, j, value -> init(i,j)}

        val bi=BuffImConvertor.instance.convert(mbi)
        val res=BuffImConvertor.instance.convert(bi)

        assertArrayEquals(res.getIntArray(),mbi.getIntArray())
    }
}