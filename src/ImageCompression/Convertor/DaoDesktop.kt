package ImageCompression.Convertor

import ImageCompression.Containers.Flag
import ImageCompression.Containers.Size
import ImageCompression.Containers.TripleDataOpcMatrix
import ImageCompression.ProcessingModules.ModuleFile
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

abstract class DaoDesktop:ConvertorDefault.IDao {
    abstract fun getFile(): File
    abstract fun getSameBase(): Size
    abstract fun getFlag():Flag

    final override fun onResultTripleData(vector: TripleDataOpcMatrix, flag: Flag) {
        val s=getSameBase()
        val fileModule=ModuleFile(getFile().absoluteFile,s.width,s.height)
        fileModule.write(vector,flag)
    }

    final override fun onResultImage(image: BufferedImage, flag: Flag) {
        val file=File(getPathWithoutType(getFile().absolutePath) + "res.bmp")
        file.createNewFile()
        ImageIO.write(image, "bmp", file)
    }

    final override fun getImage(): Pair<BufferedImage, Flag> {
        return Pair(ImageIO.read(File(getFile().absolutePath)),getFlag())
    }

    final override fun getTripleDataOpc(): Pair<TripleDataOpcMatrix, Flag> {
        val fileModule= ModuleFile(getFile())
        return fileModule.read()
    }

    private fun getPathWithoutType(path: String): String {
        return path.substring(0, path.length - 4)
    }
}