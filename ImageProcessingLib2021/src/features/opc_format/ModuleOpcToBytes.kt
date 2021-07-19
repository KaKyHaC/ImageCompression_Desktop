package features.opc_format

import data_model.processing_data.ProcessingData
import data_model.types.ByteVector
import features.AbsDataProcessor
import features.opc_format.manager.OpcBasesToBytesManager
import features.opc_format.manager.OpcToBytesManager

class ModuleOpcToBytes(
        val parameters: Parameters = Parameters()
) : AbsDataProcessor<ProcessingData.Opc2, ProcessingData.Bytes>(
        ProcessingData.Opc2::class, ProcessingData.Bytes::class
) {
    class Parameters(
            val basesParams: OpcBasesToBytesManager.Parameters = OpcBasesToBytesManager.Parameters(),
            val opcParams: OpcToBytesManager.Parameters = OpcToBytesManager.Parameters()
    )

    private val baseManager = parameters.basesParams.let { OpcBasesToBytesManager(it) }
    private val opcManager = parameters.opcParams.let { OpcToBytesManager(it) }

    override fun processDirectTyped(data: ProcessingData.Opc2): ProcessingData.Bytes {
        val byteVector = ByteVector()
        baseManager.direct(byteVector, data.triple.map { it.map { i, j, dataOpc -> dataOpc.base } }) //todo remove map
        val len1 = byteVector.getBytes().size
        opcManager.direct(byteVector, data.triple)
        val len2 = byteVector.getBytes().size - len1
        println("len1 = ${len1}, len2 = $len2")
        return ProcessingData.Bytes(byteVector)
    }

    override fun processReverseTyped(data: ProcessingData.Bytes): ProcessingData.Opc2 {
        val reader = data.byteVector.getReader()
        val bases = baseManager.reverse(reader)
        val opcs = opcManager.reverse(reader, bases)
        return ProcessingData.Opc2(opcs)
    }
}