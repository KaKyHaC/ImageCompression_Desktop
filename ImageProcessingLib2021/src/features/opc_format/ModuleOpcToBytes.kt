package features.opc_format

import data_model.processing_data.ProcessingData
import data_model.types.ByteVector
import features.AbsDataProcessor
import features.opc_format.manager.OpcBasesToBytesManager
import features.opc_format.manager.OpcToBytesManager

class ModuleOpcToBytes : AbsDataProcessor<ProcessingData.Opc2, ProcessingData.Bytes>(
        ProcessingData.Opc2::class, ProcessingData.Bytes::class
) {
    //todo add parameters

    private val baseManager = OpcBasesToBytesManager()
    private val opcManager = OpcToBytesManager()

    override fun processDirectTyped(data: ProcessingData.Opc2): ProcessingData.Bytes {
        val byteVector = ByteVector()
        baseManager.direct(byteVector, data.triple.map { it.map { i, j, dataOpc -> dataOpc.base } }) //todo remove map
        opcManager.direct(byteVector, data.triple)
        return ProcessingData.Bytes(byteVector)
    }

    override fun processReverseTyped(data: ProcessingData.Bytes): ProcessingData.Opc2 {
        val reader = data.byteVector.getReader()
        val bases = baseManager.reverse(reader)
        val opcs = opcManager.reverse(reader, bases)
        return ProcessingData.Opc2(opcs)
    }
}