package ImageCompression.Steganography

import ImageCompression.Steganography.Containers.IContainer
import ImageCompression.Steganography.Containers.OpcContainer
import ImageCompression.Steganography.Utils.ImageProcessorUtils
import ImageCompression.Steganography.Utils.OpcsParser
import ImageCompression.Utils.Objects.ByteVector
import ImageCompression.Utils.Objects.ByteVectorFile
import ImageCompression.Utils.Objects.Flag
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

class StegoModule {
    val ipu=ImageProcessorUtils()
    val opcsParser=OpcsParser()
    fun direct(from:File,to:File,unit_W:Int,unit_H:Int,message:BooleanArray?){
        val image=ImageIO.read(from)
        val units=ipu.imageToUnits(image,unit_W, unit_H)
        message?.let { val index=ipu.setMessage(units,message)}
        val opcs=ipu.directStego(units)
        val vector=ByteVector()
        opcsParser.toByteVector(vector,opcs, OpcsParser.Info(image.width,image.height,unit_W,unit_H))
        ByteVectorFile(to).write(vector, Flag(0))
    }
    fun reverse(from: File,to: File,unit_W: Int,unit_H: Int,isMultiTWO:Boolean): BooleanArray {
        val (vect,flag)=ByteVectorFile(from).read()
        val (opcs,info) = opcsParser.fromByteVector(vect)
        val units=ipu.reverceStego(opcs, unit_W, unit_H, isMultiTWO)
        val image=ipu.unitsToImage(units, info.width,info.height)
        val message=ipu.getMessage(units)
        to.createNewFile()
        ImageIO.write(image,".bmp",to)
        return message
    }
}