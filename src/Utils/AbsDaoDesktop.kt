package Utils

import ImageCompressionLib.Data.ByteVectorContainer
import ImageCompressionLib.Data.Parameters
import ImageCompressionLib.Data.Type.MyBufferedImage
import ImageCompressionLib.Convertor.ConvertorDefault
import ImageCompressionLib.ProcessingModules.ModuleFile
import java.io.File
import javax.imageio.ImageIO

abstract class AbsDaoDesktop : ConvertorDefault.IDao {
    abstract fun getFileOriginal(): File
    abstract fun getFileTarget():File
    abstract fun getParameters(): Parameters

    final override fun onResultByteVectorContainer(vector: ByteVectorContainer) {
        val fileModule=ModuleFile(getFileTarget().absoluteFile)
        fileModule.write(vector,getParameters().flag)//TODO remove
    }

    final override fun onResultImage(image: MyBufferedImage, parameters: Parameters) {
        val file=File(getPathWithoutType(getFileTarget().absolutePath) + "res.bmp")
        file.createNewFile()
        val bi=BuffImConvertor.instance.convert(image)
        ImageIO.write(bi, "bmp", file)
    }

    final override fun getImage(): Pair<MyBufferedImage,Parameters> {
        val im =ImageIO.read(File(getFileOriginal().absolutePath))
        val mb=BuffImConvertor.instance.convert(im)
        return Pair(mb,getParameters())
    }

    final override fun getByteVectorContainer(): ByteVectorContainer {
        val fileModule= ModuleFile(getFileOriginal())
        return fileModule.read()
    }

    private fun getPathWithoutType(path: String): String {
        return path.substring(0, path.length - 4)
    }
}