package features.read_image

import data_model.processing_data.ProcessingData
import features.AbsDataProcessor

class ModuleReadImage() : AbsDataProcessor<ProcessingData.File, ProcessingData.Image>(
        ProcessingData.File::class, ProcessingData.Image::class
) {

    override fun processDirectTyped(data: ProcessingData.File): ProcessingData.Image {
        TODO("Not yet implemented")
    }

    override fun processReverseTyped(data: ProcessingData.Image): ProcessingData.File {
        TODO("Not yet implemented")
    }
}