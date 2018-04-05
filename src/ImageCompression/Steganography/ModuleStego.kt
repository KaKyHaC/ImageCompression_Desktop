package ImageCompression.Steganography

import ImageCompression.Containers.ByteVector
import ImageCompression.Containers.Flag
import ImageCompression.Steganography.Containers.IContainer
import ImageCompression.Steganography.Containers.OpcContainer
import ImageCompression.Steganography.Containers.UnitContainer
import ImageCompression.Steganography.Utils.ImageProcessorUtils
import ImageCompression.Steganography.Utils.OpcsParser
import ImageCompression.Utils.Objects.ByteVectorFile
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

class ModuleStego(val dao: IDao) {
    interface IDao{
        fun getUnitContainer():ImageProcessorUtils.Triple<IContainer<UnitContainer<Short>>>
        fun getOpcContainer():ImageProcessorUtils.Triple<IContainer<OpcContainer<Short>>>
        fun onOpcResult(result:ImageProcessorUtils.Triple<IContainer<OpcContainer<Short>>>)
        fun onUnitResult(result:ImageProcessorUtils.Triple<IContainer<UnitContainer<Short>>>)
        fun onMessageRead(message: BooleanArray?)
    }

    val ipu= ImageProcessorUtils()
    fun direct(message:BooleanArray?, isDivTwo:Boolean){
        val units=dao.getUnitContainer()
        message?.let { val index=ipu.setMessage(units,message)}
        val opcs=ipu.directStego(units,isDivTwo)
        dao.onOpcResult(opcs)
    }
    fun reverse(unit_W: Int, unit_H: Int, isMultiTWO:Boolean) {
        val opcs=dao.getOpcContainer()
        val units=ipu.reverceStego(opcs, unit_W, unit_H, isMultiTWO)
        dao.onUnitResult(units)
        val message=ipu.getMessage(units)
        dao.onMessageRead(message)
    }
}