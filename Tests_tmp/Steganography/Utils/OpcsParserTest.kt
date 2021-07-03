package Steganography.Utils

import Steganography.Containers.Container
import Steganography.Containers.IContainer
import Steganography.Containers.OpcContainer
import ImageCompressionLib.Containers.Type.ByteVector
import org.junit.Assert.*
import org.junit.Test
import java.math.BigInteger

class OpcsParserTest(){
    val par= OpcsParser()

    @Test
    fun test1(){
        test(10,10,10,10)
    }
    fun test(w:Int,h:Int,uW:Int,uH:Int){
        val info= OpcsParser.Info(w,h,uW,uH)

        val res= ImageProcessorUtils.Triple<IContainer<OpcContainer<Short>>>(null,null,null)
        res.r= Container<OpcContainer<Short>>(w.toInt(), h.toInt()) { i, j -> OpcContainer(BigInteger((i + j).toString()), Array(uH) { (10).toShort() }) }
        res.g= Container<OpcContainer<Short>>(w.toInt(), h.toInt()) { i, j -> OpcContainer(BigInteger((i + j).toString()), Array(uH) { (10).toShort() }) }
        res.b= Container<OpcContainer<Short>>(w.toInt(), h.toInt()) { i, j -> OpcContainer(BigInteger((i + j).toString()), Array(uH) { (10).toShort() }) }

        val bv= ByteVector()

        par.toByteVector(bv,res,info)
        val (opcs,info1) = par.fromByteVector(bv)

        assertTrue(info.equals(info1))
    }
}