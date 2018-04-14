package ImageCompressionLib.Steganography.Utils

import ImageCompressionLib.Steganography.Containers.Container
import ImageCompressionLib.Steganography.Containers.IContainer
import ImageCompressionLib.Steganography.Containers.OpcContainer
import ImageCompressionLib.Containers.ByteVector
import java.math.BigInteger

class OpcsParser{
    data class Info(val width:Int,val height:Int,val unit_W:Int,val unit_H: Int)
    fun toByteVector(vector: ByteVector, opcs:ImageProcessorUtils.Triple<IContainer<OpcContainer<Short>>>, info:Info){
        infoToVector(info,vector)
        vector.append(opcs.r?.width!!.toShort())
        vector.append(opcs.r?.height!!.toShort())
        opcs.r?.writeToBV(vector,info)
        opcs.g?.writeToBV(vector,info)
        opcs.b?.writeToBV(vector,info)

    }
    fun fromByteVector(vector: ByteVector):Pair<ImageProcessorUtils.Triple<IContainer<OpcContainer<Short>>>,Info>{
        val info=infoFromVector(vector)
        val w=vector.getNextShort()
        val h=vector.getNextShort()
        val res=ImageProcessorUtils.Triple<IContainer<OpcContainer<Short>>>(null,null,null)
        res.r=Container<OpcContainer<Short>>(w.toInt(),h.toInt()){i,j-> OpcContainer(BigInteger("0"), Array(0){(0).toShort()}) }
        res.g=Container<OpcContainer<Short>>(w.toInt(),h.toInt()){i,j-> OpcContainer(BigInteger("0"), Array(0){(0).toShort()}) }
        res.b=Container<OpcContainer<Short>>(w.toInt(),h.toInt()){i,j-> OpcContainer(BigInteger("0"), Array(0){(0).toShort()}) }

        res.r!!.readFromBV(vector,info)
        res.g!!.readFromBV(vector,info)
        res.b!!.readFromBV(vector,info)

        return Pair(res,info)
    }
    private fun IContainer<OpcContainer<Short>>.writeToBV(byteVector: ByteVector, info: Info){
        forEach { el ->
            el?.writeToBV(byteVector,info)
            el
        }
    }
    private fun IContainer<OpcContainer<Short>>.readFromBV(byteVector: ByteVector, info: Info){
        forEach { el ->
            el?.readFromBV(byteVector,info)
            el
        }
    }
    private fun OpcContainer<Short>.writeToBV(byteVector: ByteVector, info: Info){
        val base=this.base
        val codeLen=getMaxCodeLen(base,info)
        val bytes=this.code.toByteArray()

        for(i in 0 until info.unit_H){
            byteVector.append(base[i])
        }
        for(i in bytes.size..codeLen-1)
            byteVector.append((0).toByte())

        for(i in this.code.toByteArray())
            byteVector.append(i)

    }
    private fun OpcContainer<Short>.readFromBV(byteVector: ByteVector, info: Info){
        base= Array(info.unit_H){(0).toShort()}
        for(i in 0 until info.unit_H){
            base[i]=byteVector.getNextShort()
        }
        val codeLen=getMaxCodeLen(base, info)
        val bytes=ByteArray(codeLen)
        for(i in 0 until codeLen)
            bytes[i]=byteVector.getNext()

        code= BigInteger(bytes)
    }
    private fun getMaxCodeLen(base:Array<Short>,info: Info): Int {
        var maxBase=BigInteger.ONE
        for(i in 0 .. info.unit_W-1){
            for(j in 0..info.unit_H-1){
                maxBase*=BigInteger(base[j].toString())
            }
        }
        return maxBase.toByteArray().size
    }
    private fun infoToVector(info:Info,byteVector: ByteVector){
        byteVector.append(info.width.toShort())
        byteVector.append(info.height.toShort())
        byteVector.append(info.unit_W.toShort())
        byteVector.append(info.unit_H.toShort())
    }
    private fun infoFromVector(byteVector: ByteVector):Info{
        val w=byteVector.getNextShort().toInt()
        val h=byteVector.getNextShort().toInt()
        val uw=byteVector.getNextShort().toInt()
        val uh=byteVector.getNextShort().toInt()
        return Info(w,h,uw,uh)
    }
}