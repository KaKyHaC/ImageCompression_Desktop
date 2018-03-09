package ImageCompression.Steganography

import ImageCompression.Steganography.Containers.IContainer
import ImageCompression.Steganography.Containers.OpcContainer
import ImageCompression.Steganography.Utils.ImageProcessorUtils
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

class StegoModule {
    fun direct(file:String,unit_W:Int,unit_H:Int,message:BooleanArray): ImageProcessorUtils.Triple<IContainer<OpcContainer<Short>>> {
        val image=ImageIO.read(File(file))
        val ipu=ImageProcessorUtils()
        val units=ipu.imageToUnits(image,unit_W, unit_H)
        val index=ipu.setMessage(units,message)
        val opcs=ipu.directStego(units)
        return opcs
    }
    fun reverse(opcs:ImageProcessorUtils.Triple<IContainer<OpcContainer<Short>>>,width:Int,height:Int,unit_W: Int,unit_H: Int,isMultiTWO:Boolean,file:String): BooleanArray {
        val ipu=ImageProcessorUtils()
        val units=ipu.reverceStego(opcs, unit_W, unit_H, isMultiTWO)
        val image=ipu.unitsToImage(units, width, height)
        val message=ipu.getMessage(units)
        File(file).createNewFile()
        ImageIO.write(image,".bmp",File(file))
        return message
    }
}