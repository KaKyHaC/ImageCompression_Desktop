package ImageCompressionLib.Steganography.Utils

import ImageCompressionLib.Steganography.Containers.Container
import ImageCompressionLib.Steganography.Containers.IContainer
import ImageCompressionLib.Steganography.Containers.OpcContainer
import ImageCompressionLib.Steganography.Containers.UnitContainer
import java.awt.Color
import java.awt.image.BufferedImage
import java.util.ArrayList
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors
import java.util.concurrent.Future

class ImageProcessorUtils {
    data class Triple<T>(var r:T?,var g:T?,var b:T?)

    fun imageToUnits(image:BufferedImage,unit_W: Int,unit_H: Int):
            Triple<IContainer<UnitContainer<Short>>> {
        val r=Container<Short>(image.width,image.height){i,j->Color(image.getRGB(i,j)).red.toShort()}
        val g=Container<Short>(image.width,image.height){i,j->Color(image.getRGB(i,j)).green.toShort()}
        val b=Container<Short>(image.width,image.height){i,j->Color(image.getRGB(i,j)).blue.toShort()}

        val units= Triple<IContainer<UnitContainer<Short>>>(null, null, null)

        val executorService = Executors.newFixedThreadPool(3)
        val futures = ArrayList<Future<IContainer<UnitContainer<Short>>>>()

        futures.add(executorService.submit<IContainer<UnitContainer<Short>>> {  UnitContainerFactory.getContainers(r, unit_W, unit_H)})
        futures.add(executorService.submit<IContainer<UnitContainer<Short>>> {  UnitContainerFactory.getContainers(g, unit_W, unit_H)})
        futures.add(executorService.submit<IContainer<UnitContainer<Short>>> {  UnitContainerFactory.getContainers(b, unit_W, unit_H)})

        try {
            units.r= futures[0].get()
            units.g = futures[1].get()
            units.b = futures[2].get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: ExecutionException) {
            e.printStackTrace()
        }

        return units
    }
    fun unitsToImage(units: Triple<IContainer<UnitContainer<Short>>>, width:Int, height:Int):
            BufferedImage {
        val image=BufferedImage(width,height,BufferedImage.TYPE_3BYTE_BGR)

        val executorService = Executors.newFixedThreadPool(3)
        val futures = ArrayList<Future<IContainer<Short>>>()

        futures.add(executorService.submit<IContainer<Short>> {  UnitContainerFactory.getData(units.r!!,width,height)})
        futures.add(executorService.submit<IContainer<Short>> {  UnitContainerFactory.getData(units.g!!,width, height)})
        futures.add(executorService.submit<IContainer<Short>> {  UnitContainerFactory.getData(units.b!!,width, height)})

        var r:IContainer<Short>?=null
        var g:IContainer<Short>?=null
        var b:IContainer<Short>?=null

        try {
            r= futures[0].get()
            g = futures[1].get()
            b = futures[2].get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: ExecutionException) {
            e.printStackTrace()
        }

        for (i in 0..width-1)
            for(j in 0..height-1){
                val color=Color(r!![i,j]!!.toInt(),g!![i,j]!!.toInt(),b!![i,j]!!.toInt())
                image.setRGB(i,j,color.rgb)
            }
        return image
    }

    fun directStego(units: Triple<IContainer<UnitContainer<Short>>>,isDivTWO: Boolean):
            Triple<IContainer<OpcContainer<Short>>> {
        val opcs= Triple<IContainer<OpcContainer<Short>>>(null, null, null)

        val executorService = Executors.newFixedThreadPool(3)
        val futures = ArrayList<Future<IContainer<OpcContainer<Short>>>>()

        futures.add(executorService.submit<IContainer<OpcContainer<Short>>> {  directConvert(units.r!!,isDivTWO)})
        futures.add(executorService.submit<IContainer<OpcContainer<Short>>> {  directConvert(units.g!!,isDivTWO)})
        futures.add(executorService.submit<IContainer<OpcContainer<Short>>> {  directConvert(units.b!!,isDivTWO)})

        try {
            opcs.r= futures[0].get()
            opcs.g = futures[1].get()
            opcs.b = futures[2].get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: ExecutionException) {
            e.printStackTrace()
        }

        return opcs
    }

    fun reverceStego(opcs: Triple<IContainer<OpcContainer<Short>>>,
                     unit_W: Int, unit_H: Int, isMultiTWO: Boolean):
            Triple<IContainer<UnitContainer<Short>>> {
        val units= Triple<IContainer<UnitContainer<Short>>>(null, null, null)

        val executorService = Executors.newFixedThreadPool(3)
        val futures = ArrayList<Future<IContainer<UnitContainer<Short>>>>()

        futures.add(executorService.submit<IContainer<UnitContainer<Short>>> {  reverceConvert(opcs.r!!,unit_W, unit_H, isMultiTWO)})
        futures.add(executorService.submit<IContainer<UnitContainer<Short>>> {  reverceConvert(opcs.g!!,unit_W, unit_H, isMultiTWO)})
        futures.add(executorService.submit<IContainer<UnitContainer<Short>>> {  reverceConvert(opcs.b!!,unit_W, unit_H, isMultiTWO)})

        try {
            units.r= futures[0].get()
            units.g = futures[1].get()
            units.b = futures[2].get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: ExecutionException) {
            e.printStackTrace()
        }


        return units
    }
    fun reverceStegoOneThread(opcs: Triple<IContainer<OpcContainer<Short>>>,
                     unit_W: Int, unit_H: Int, isMultiTWO: Boolean):
            Triple<IContainer<UnitContainer<Short>>> {
        val units= Triple<IContainer<UnitContainer<Short>>>(null, null, null)
        units.r=reverceConvert(opcs.r!!,unit_W,unit_H,isMultiTWO)
        units.g=reverceConvert(opcs.g!!,unit_W,unit_H,isMultiTWO)
        units.b=reverceConvert(opcs.b!!,unit_W,unit_H,isMultiTWO)
        return units
    }


    fun getMessageMaxSize(units: Triple<IContainer<UnitContainer<Short>>>):Int{
        var len=0
        units.r?.let { len+=units.r!!.width*units.r!!.height }
        units.g?.let { len+=units.g!!.width*units.g!!.height }
        units.b?.let { len+=units.b!!.width*units.b!!.height }
        return len
    }

    fun setMessage(units: Triple<IContainer<UnitContainer<Short>>>,
                   message: BooleanArray):Int{
        var index=units.r?.setMesasge(message,0)?:0
        index=units.g?.setMesasge(message,index)?:index
        index=units.b?.setMesasge(message,index)?:index
        return index
    }
    fun getMessage(units: Triple<IContainer<UnitContainer<Short>>>):
            BooleanArray{
        val message=BooleanArray(getMessageMaxSize(units))
        var index=units.r?.readMessage(message,0)?:0
        index=units.g?.readMessage(message,index)?:index
        index=units.b?.readMessage(message,index)?:index
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

    private fun directConvert(org:IContainer<UnitContainer<Short>>,isDivTWO:Boolean):IContainer<OpcContainer<Short>>{
        val res=Container<OpcContainer<Short>>(org.width,org.height)
        val ins= StegoConvertor.instance
        org.forEach { w, h ->
            res[w,h]=ins.direct(org[w,h]!!,isDivTWO)
            null
        }
        return res
    }
    private fun reverceConvert(org:IContainer<OpcContainer<Short>>,
                               unit_W: Int,unit_H: Int,isMultiTWO:Boolean):
            IContainer<UnitContainer<Short>>{
        val res=Container<UnitContainer<Short>>(org.width,org.height)
        val ins= StegoConvertor.instance
        org.forEach { w, h ->
            res[w,h]=ins.reverce(org[w,h]!!,unit_W,unit_H,isMultiTWO)
            null
        }
        return res
    }
}