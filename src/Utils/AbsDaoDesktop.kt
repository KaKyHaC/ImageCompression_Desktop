package Utils

import ImageCompressionLib.Containers.ByteVectorContainer
import ImageCompressionLib.Containers.Type.Flag
import ImageCompressionLib.Containers.Type.MyBufferedImage
import ImageCompressionLib.Convertor.ConvertorDefault
import ImageCompressionLib.ProcessingModules.ModuleFile
import java.io.File
import javax.imageio.ImageIO

abstract class AbsDaoDesktop : ConvertorDefault.IDao {
    abstract fun getFileOriginal(): File
    abstract fun getFileTarget():File
    abstract fun getFlag(): Flag

    final override fun onResultByteVectorContainer(vector: ByteVectorContainer, flag: Flag) {
        val fileModule=ModuleFile(getFileTarget().absoluteFile)
        fileModule.write(vector,flag)
    }

    final override fun onResultImage(image: MyBufferedImage, flag: Flag) {
        val file=File(getPathWithoutType(getFileTarget().absolutePath) + "res.bmp")
        file.createNewFile()
        val bi=BuffImConvertor.instance.convert(image)
        ImageIO.write(bi, "bmp", file)
    }

    final override fun getImage(): Pair<MyBufferedImage, Flag> {
        val im =ImageIO.read(File(getFileOriginal().absolutePath))
        val mb=BuffImConvertor.instance.convert(im)
        return Pair(mb,getFlag())
    }

    final override fun getByteVectorContainer(): Pair<ByteVectorContainer, Flag> {
        val fileModule= ModuleFile(getFileOriginal())
        return fileModule.read()
    }

    private fun getPathWithoutType(path: String): String {
        return path.substring(0, path.length - 4)
    }
}