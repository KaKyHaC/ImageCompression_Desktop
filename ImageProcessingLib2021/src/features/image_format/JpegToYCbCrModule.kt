package features.image_format

import data_model.processing_data.ProcessingData
import features.AbsDataProcessor

class JpegToYCbCrModule() : AbsDataProcessor<ProcessingData.Image.Jpeg, ProcessingData.Image.YCbCr>(
        ProcessingData.Image.Jpeg::class, ProcessingData.Image.YCbCr::class
) {

    override fun processDirectTyped(data: ProcessingData.Image.Jpeg): ProcessingData.Image.YCbCr {
        TODO("Not yet implemented")
    }

    override fun processReverseTyped(data: ProcessingData.Image.YCbCr): ProcessingData.Image.Jpeg {
        TODO("Not yet implemented")
    }
}