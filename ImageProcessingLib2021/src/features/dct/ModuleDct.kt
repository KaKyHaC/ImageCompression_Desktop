package features.dct

import data_model.processing_data.ProcessingData
import features.AbsDataProcessor

class ModuleDct : AbsDataProcessor<ProcessingData.Image, ProcessingData.Image>(
        ProcessingData.Image::class, ProcessingData.Image::class
) {
    override fun processDirectTyped(data: ProcessingData.Image): ProcessingData.Image {
        TODO("Not yet implemented")
    }

    override fun processReverseTyped(data: ProcessingData.Image): ProcessingData.Image {
        TODO("Not yet implemented")
    }
}