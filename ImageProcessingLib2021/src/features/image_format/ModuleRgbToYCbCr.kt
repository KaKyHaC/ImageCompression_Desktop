package features.image_format

import data_model.generics.Triple
import data_model.processing_data.ProcessingData
import features.AbsDataProcessor
import features.image_format.data.RGB
import features.image_format.data.YCbCr
import features.image_format.utils.RgbToYbrUtils

class ModuleRgbToYCbCr() : AbsDataProcessor<ProcessingData.Image, ProcessingData.Image>(
        ProcessingData.Image::class, ProcessingData.Image::class
) {

    override fun processDirectTyped(data: ProcessingData.Image): ProcessingData.Image {
        val rgb = RGB(data.triple.first, data.triple.second, data.triple.third)
        val yCbCr = RgbToYbrUtils.direct(rgb)
        return ProcessingData.Image(
                Triple(
                        yCbCr.matrixY,
                        yCbCr.matrixCb,
                        yCbCr.matrixCr
                )
        )
    }

    override fun processReverseTyped(data: ProcessingData.Image): ProcessingData.Image {
        val yCbCr = YCbCr(data.triple.first, data.triple.second, data.triple.third)
        val rgb = RgbToYbrUtils.reverse(yCbCr)
        return ProcessingData.Image(
                Triple(
                        rgb.matrixR,
                        rgb.matrixG,
                        rgb.matrixB
                )
        )
    }
}