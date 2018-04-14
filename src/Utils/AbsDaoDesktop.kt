package ImageCompressionLib.Convertor.Implementations

import ImageCompressionLib.Containers.ByteVectorContainer
import ImageCompressionLib.Containers.Flag
import ImageCompressionLib.Containers.Size
import ImageCompressionLib.Convertor.ConvertorDefault
import ImageCompressionLib.ProcessingModules.ModuleFile
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

abstract class AbsDaoDesktop : ConvertorDefault.IDao {
    abstract fun getFileOriginal(): File
    abstract fun getFileTarget():File
    abstract fun getFlag():Flag

    final override fun onResultByteVectorContainer(vector: ByteVectorContainer, flag: Flag) {
        val fileModule=ModuleFile(getFileTarget().absoluteFile)
        fileModule.write(vector,flag)
    }

    final override fun onResultImage(image: BufferedImage, flag: Flag) {
        val file=File(getPathWithoutType(getFileTarget().absolutePath) + "res.bmp")
        file.createNewFile()
        ImageIO.write(image, "bmp", file)
    }

    final override fun getImage(): Pair<BufferedImage, Flag> {
        return Pair(ImageIO.read(File(getFileOriginal().absolutePath)),getFlag())
    }

    final override fun getByteVectorContainer(): Pair<ByteVectorContainer, Flag> {
        val fileModule= ModuleFile(getFileOriginal())
        return fileModule.read()
    }

    private fun getPathWithoutType(path: String): String {
        return path.substring(0, path.length - 4)
    }
}