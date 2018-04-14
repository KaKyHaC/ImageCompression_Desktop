package ImageCompressionLib.Steganography.Utils

import ImageCompressionLib.Steganography.Containers.OpcContainer
import ImageCompressionLib.Steganography.Containers.UnitContainer
import java.math.BigInteger

class StegoConvertor private constructor(){
    companion object {
        @JvmField val instance= StegoConvertor()
    }

    fun direct(data: UnitContainer<Short>, isDivTWO:Boolean): OpcContainer<Short> {
        var curBase=BigInteger.ONE
        var code=BigInteger.ZERO
        val base=getBase(data)
//        var count=0


        reverceForEach(data.width,data.height){ i: Int, j: Int ->
            code+=curBase.multiply(BigInteger.valueOf(data[i,j]?.toLong()?:0))
            curBase*=(BigInteger.valueOf(base[j].toLong()))
        }

        if(data.message)
            code+=curBase

        if(isDivTWO)code/=BigInteger.TWO

        return OpcContainer(code, base)
    }
    fun reverce(data: OpcContainer<Short>, width:Int, height:Int, isMultTWO:Boolean): UnitContainer<Short> {
        var curBase=BigInteger.ONE
        var nextBase=BigInteger.ONE
        val base=data.base
        val code=if(isMultTWO)data.code* BigInteger.TWO else data.code


        val res=Array<Array<Short>>(width){ShortArray(height).toTypedArray()}

        reverceForEach(width,height){ i: Int, j: Int ->
            val b=BigInteger.valueOf(base[j].toLong())
            if(b== BigInteger.ZERO)
                throw Exception("BI = 0")
            nextBase=curBase*b
            res[i][j]=((code/curBase)-(code/nextBase)*b).toShort()
            curBase=nextBase
        }
        val message=(((code/curBase)-(code/(curBase* BigInteger.TWO))* BigInteger.TWO).toInt()==1)
        return UnitContainer(res, message)
    }

    private fun getBase(data: UnitContainer<Short>):Array<Short>{
        val res=Array<Short>(data.height){1}
//        for((i,e) in data.data.withIndex()){
//            for((j, el) in e.withIndex())
//                if(res[i]<el)res[i]=(el+1).toShort()
//        }
        for(i in 0..data.width-1){
            for(j in 0..data.height-1){
                if(res[j]<=data[i,j]!!)res[j]=(data[i,j]!!+1).toShort()
            }
        }
        return res
    }
    private fun reverceForEach(width: Int,height: Int,forEach:(i:Int,j:Int)->Unit){
        for(i in width-1 downTo 0){
            for (j in height-1 downTo 0){
                forEach(i,j)
            }
        }
    }
}
