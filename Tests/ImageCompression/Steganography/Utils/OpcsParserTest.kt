package ImageCompression.Steganography.Utils

import ImageCompression.Steganography.Containers.Container
import ImageCompression.Steganography.Containers.IContainer
import ImageCompression.Steganography.Containers.OpcContainer
import ImageCompression.Utils.Objects.ByteVector
import org.junit.Assert.*
import org.junit.Test
import java.math.BigInteger

class OpcsParserTest(){
    val par=OpcsParser()

    @Test
    fun test1(){
        test(10,10,10,10)
    }
    fun test(w:Int,h:Int,uW:Int,uH:Int){
        val info= OpcsParser.Info(w,h,uW,uH)

        val res=ImageProcessorUtils.Triple<IContainer<OpcContainer<Short>>>(null,null,null)
        res.r= Container<OpcContainer<Short>>(w.toInt(),h.toInt()){ i, j-> OpcContainer(BigInteger((i+j).toString()), Array(uH){(10).toShort()}) }
        res.g= Container<OpcContainer<Short>>(w.toInt(),h.toInt()){ i, j-> OpcContainer(BigInteger((i+j).toString()), Array(uH){(10).toShort()}) }
        res.b= Container<OpcContainer<Short>>(w.toInt(),h.toInt()){ i, j-> OpcContainer(BigInteger((i+j).toString()), Array(uH){(10).toShort()}) }

        val bv=ByteVector()

        par.toByteVector(bv,res,info)
        val (opcs,info1) = par.fromByteVector(bv)

        assertTrue(info.equals(info1))
    }
}