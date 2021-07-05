package features.compression

import data_model.processing_data.ProcessingData
import features.AbsDataProcessor

class ModuleCompressionBytes : AbsDataProcessor<ProcessingData.Bytes, ProcessingData.Bytes>(
        ProcessingData.Bytes::class, ProcessingData.Bytes::class
) {
    override fun processDirectTyped(data: ProcessingData.Bytes): ProcessingData.Bytes {
        TODO("Not yet implemented")
    }

    override fun processReverseTyped(data: ProcessingData.Bytes): ProcessingData.Bytes {
        TODO("Not yet implemented")
    }
}