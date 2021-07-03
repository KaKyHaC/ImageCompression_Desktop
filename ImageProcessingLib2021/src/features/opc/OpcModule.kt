package features.opc

import data_model.processing_data.ProcessingData
import features.AbsDataProcessor

class OpcModule : AbsDataProcessor<ProcessingData.Image, ProcessingData.OPC>(
        ProcessingData.Image::class, ProcessingData.OPC::class
) {

    override fun processDirectTyped(data: ProcessingData.Image): ProcessingData.OPC {
        TODO("Not yet implemented")
    }

    override fun processReverseTyped(data: ProcessingData.OPC): ProcessingData.Image {
        TODO("Not yet implemented")
    }
}