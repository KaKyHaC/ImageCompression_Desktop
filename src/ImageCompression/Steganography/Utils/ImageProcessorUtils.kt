package ImageCompression.Steganography

import ImageCompression.Steganography.Containers.Container
import ImageCompression.Steganography.Containers.IContainer
import ImageCompression.Steganography.Containers.OpcContainer
import ImageCompression.Steganography.Containers.UnitContainer
import java.awt.Color
import java.awt.image.BufferedImage

class ImageProcessorUtils {
    data class Triple<T>(var r:T?,var g:T?,var b:T?)
////    var image:BufferedImage
////
//    constructor(image:BufferedImage):this(image.width,image.height){
//        this.image=image
//    }
//    init{
//        this.image= BufferedImage(width,height,BufferedImage.TYPE_3BYTE_BGR)
//    }
    fun imageToUnits(image:BufferedImage,unit_W: Int,unit_H: Int): Triple<IContainer<UnitContainer<Short>>> {
        val r=Container<Short>(image.width,image.height){i,j->Color(image.getRGB(i,j)).red.toShort()}
        val g=Container<Short>(image.width,image.height){i,j->Color(image.getRGB(i,j)).green.toShort()}
        val b=Container<Short>(image.width,image.height){i,j->Color(image.getRGB(i,j)).blue.toShort()}

        val units=Triple<IContainer<UnitContainer<Short>>>(null,null,null)

        units.r=UnitContainerFactory.getContainers(r,unit_W,unit_H)
        units.g=UnitContainerFactory.getContainers(g,unit_W,unit_H)
        units.b=UnitContainerFactory.getContainers(b,unit_W,unit_H)

        return units
    }
    fun directStego(units:Triple<IContainer<UnitContainer<Short>>>):Triple<IContainer<OpcContainer<Short>>>{
        val opcs=Triple<IContainer<OpcContainer<Short>>>(null,null,null)

        opcs.r=directConvert(units.r!!)
        opcs.g=directConvert(units.g!!)
        opcs.b=directConvert(units.b!!)
        return opcs
    }
    fun reverceStego(opcs:Triple<IContainer<OpcContainer<Short>>>, unit_W: Int,unit_H: Int,isMultiTWO: Boolean):Triple<IContainer<UnitContainer<Short>>>{
        val units=Triple<IContainer<UnitContainer<Short>>>(null,null,null)

        units.r=reverceConvert(opcs.r!!,unit_W, unit_H, isMultiTWO)
        units.g=reverceConvert(opcs.g!!,unit_W, unit_H, isMultiTWO)
        units.b=reverceConvert(opcs.b!!,unit_W, unit_H, isMultiTWO)
        return units
    }

    fun getMessageMaxSize(units:Triple<IContainer<UnitContainer<Short>>>):Int{
        var len=0
        units.r?.let { len+=units.r!!.width*units.r!!.height }
        units.g?.let { len+=units.g!!.width*units.g!!.height }
        units.b?.let { len+=units.b!!.width*units.b!!.height }
        return len
    }

    fun setMessage(units:Triple<IContainer<UnitContainer<Short>>>,message: BooleanArray):Int{
        var index=units.r?.setMesasge(message,0)?:0
        index=units.r?.setMesasge(message,index)?:index
        index=units.r?.setMesasge(message,index)?:index
        return index
    }
    fun getMessage(units:Triple<IContainer<UnitContainer<Short>>>):BooleanArray{
        val message=BooleanArray(getMessageMaxSize(units))
        var index=units.r?.setMesasge(message,0)?:0
        index=units.r?.setMesasge(message,index)?:index
        index=units.r?.setMesasge(message,index)?:index
        return message
    }

    fun IContainer<UnitContainer<Short>>.setMesasge(message: BooleanArray,startIndex:Int):Int{
        var index=startIndex
        this.forEach { el ->
            if(index<message.size)
                el?.message=message[index++]
            el
        }
        return index
    }
    fun IContainer<UnitContainer<Short>>.readMessage(message: BooleanArray,startIndex:Int):Int{
        var index=startIndex
        this.forEach { el ->
            if(index<message.size)
                message[index++]=el?.message?:false
            el
        }
        return index
    }

    private fun directConvert(org:IContainer<UnitContainer<Short>>):IContainer<OpcContainer<Short>>{
        val res=Container<OpcContainer<Short>>(org.width,org.height)
        val ins=StegoConvertor.instance
        org.forEach { w, h ->
            res[w,h]=ins.direct(org[w,h]!!)
            null
        }
        return res
    }
    private fun reverceConvert(org:IContainer<OpcContainer<Short>>,unit_W: Int,unit_H: Int,isMultiTWO:Boolean):IContainer<UnitContainer<Short>>{
        val res=Container<UnitContainer<Short>>(org.width,org.height)
        val ins=StegoConvertor.instance
        org.forEach { w, h ->
            res[w,h]=ins.reverce(org[w,h]!!,unit_W,unit_H,isMultiTWO)
            null
        }
        return res
    }

}