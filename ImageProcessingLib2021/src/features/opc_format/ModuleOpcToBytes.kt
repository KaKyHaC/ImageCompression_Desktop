package features.opc_format

import data_model.processing_data.ProcessingData
import features.AbsDataProcessor

class ModuleOpcToBytes : AbsDataProcessor<ProcessingData.OPC, ProcessingData.Bytes>(
        ProcessingData.OPC::class, ProcessingData.Bytes::class
) {

    override fun processDirectTyped(data: ProcessingData.OPC): ProcessingData.Bytes {
        TODO("Not yet implemented")
    }

    override fun processReverseTyped(data: ProcessingData.Bytes): ProcessingData.OPC {
        TODO("Not yet implemented")
    }
}