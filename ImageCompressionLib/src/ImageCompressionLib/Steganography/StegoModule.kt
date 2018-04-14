package ImageCompressionLib.Steganography

import ImageCompressionLib.Steganography.Utils.ImageProcessorUtils
import ImageCompressionLib.Steganography.Utils.OpcsParser
import ImageCompressionLib.Containers.ByteVector
import ImageCompressionLib.Utils.Objects.ByteVectorFile
import ImageCompressionLib.Containers.Flag
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

class StegoModule {
    val ipu=ImageProcessorUtils()
    val opcsParser=OpcsParser()
    fun direct(from:File,to:File,unit_W:Int,unit_H:Int,message:BooleanArray?,isDivTwo:Boolean){
        val image=ImageIO.read(from)
        val units=ipu.imageToUnits(image,unit_W, unit_H)
        message?.let { val index=ipu.setMessage(units,message)}
        val opcs=ipu.directStego(units,isDivTwo)
        val vector= ByteVector()
        opcsParser.toByteVector(vector,opcs, OpcsParser.Info(image.width,image.height,unit_W,unit_H))
        ByteVectorFile(to).write(vector, Flag(0))
    }
    fun reverse(from: File,to: File,unit_W: Int,unit_H: Int,isMultiTWO:Boolean): BooleanArray {
        val (vect,flag)=ByteVectorFile(from).read()
        val (opcs,info) = opcsParser.fromByteVector(vect)
        val units=ipu.reverceStego(opcs, unit_W, unit_H, isMultiTWO)
        val image=ipu.unitsToImage(units, info.width,info.height)
        val message=ipu.getMessage(units)
        try{
            to.createNewFile()
            ImageIO.write(image,"BMP",to)
        }catch (e:Exception){e.printStackTrace()}
        return message
    }
    fun getImageFromStego(from: File,unit_W: Int,unit_H: Int,isMultiTWO:Boolean):BufferedImage{
        val (vect,flag)=ByteVectorFile(from).read()
        val (opcs,info) = opcsParser.fromByteVector(vect)
        val units=ipu.reverceStego(opcs, unit_W, unit_H, isMultiTWO)
        val image=ipu.unitsToImage(units, info.width,info.height)
        return image
    }
    fun getMessageFromImage(from: File,unit_W: Int,unit_H: Int,isMultiTWO:Boolean):BooleanArray{
        val image=ImageIO.read(from)
        val units=ipu.imageToUnits(image,unit_W, unit_H)
        return ipu.getMessage(units)
    }
}