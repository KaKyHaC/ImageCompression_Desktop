package features.write_bytes

import data_model.processing_data.ProcessingData
import features.AbsDataProcessor

class ModuleWriteBytes : AbsDataProcessor<ProcessingData.Bytes, ProcessingData.File>(
        ProcessingData.Bytes::class, ProcessingData.File::class
) {

    override fun processDirectTyped(data: ProcessingData.Bytes): ProcessingData.File {
        TODO("Not yet implemented")
    }

    override fun processReverseTyped(data: ProcessingData.File): ProcessingData.Bytes {
        TODO("Not yet implemented")
    }
}