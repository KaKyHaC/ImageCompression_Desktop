package features.opc_format

import data_model.processing_data.ProcessingData
import features.AbsDataProcessor

class ModuleOpcToBytes : AbsDataProcessor<ProcessingData.Opc, ProcessingData.Bytes>(
        ProcessingData.Opc::class, ProcessingData.Bytes::class
) {

    override fun processDirectTyped(data: ProcessingData.Opc): ProcessingData.Bytes {
        TODO("Not yet implemented")
    }

    override fun processReverseTyped(data: ProcessingData.Bytes): ProcessingData.Opc {
        TODO("Not yet implemented")
    }
}