package features.read_image

import data_model.processing_data.ProcessingData
import features.AbsDataProcessor
import features.read_image.utils.ReadBmpUtils
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

class ModuleReadImage(
        val parameters: Parameters = Parameters()
) : AbsDataProcessor<ProcessingData.File, ProcessingData.Image>(
        ProcessingData.File::class, ProcessingData.Image::class
) {

    data class Parameters(
            val fileName: String = "image.bmp"
    )

    override fun processDirectTyped(data: ProcessingData.File): ProcessingData.Image {
        val file = File(parameters.fileName)
        val bufferedImage = ImageIO.read(file)
        val map = ReadBmpUtils.map(bufferedImage)
        return ProcessingData.Image(map)
    }

    override fun processReverseTyped(data: ProcessingData.Image): ProcessingData.File {
        val file = File(parameters.fileName)
        file.createNewFile()
        val map = ReadBmpUtils.map(data.triple)
        ImageIO.write(map, "bmp", file.outputStream())
        return ProcessingData.File(file)
    }
}