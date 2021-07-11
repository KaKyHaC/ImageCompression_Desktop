package features.opc2_god_format

import data_model.processing_data.ProcessingData
import data_model.types.ByteVector
import features.AbsDataProcessor
import features.opc2.ModuleBasesOpc2
import features.opc_format.manager.OpcBasesToBytesManager
import features.opc_format.manager.OpcToBytesManager

class ModuleOpc2God(
        val parameters: Parameters = Parameters()
) : AbsDataProcessor<ProcessingData.Opc2, ProcessingData.Bytes>(
        ProcessingData.Opc2::class, ProcessingData.Bytes::class
) {
    data class Parameters(
            val basesModuleParams: ModuleBasesOpc2.Parameters = ModuleBasesOpc2.Parameters(),
            val basesParams: OpcBasesToBytesManager.Parameters = OpcBasesToBytesManager.Parameters(),
            val opcParams: OpcToBytesManager.Parameters = OpcToBytesManager.Parameters()
    )

    private val moduleBasesOpc2 = ModuleBasesOpc2(parameters.basesModuleParams)
    private val baseManager = parameters.basesParams.let { OpcBasesToBytesManager(it) }
    private val opcManager = parameters.opcParams.let { OpcToBytesManager(it) }


    override fun processDirectTyped(data: ProcessingData.Opc2): ProcessingData.Bytes {
        val byteVector = ByteVector()
        val basesData = ProcessingData.Opc2.Bases(data)
        val basesOpc = moduleBasesOpc2.processDirectTyped(basesData)
        val basesOpcBase = basesOpc.triple.map { it.map { i, j, value -> value.base } }
        baseManager.direct(byteVector, basesOpcBase)
        opcManager.direct(byteVector, basesOpc.triple)
        opcManager.direct(byteVector, data.triple)
        return ProcessingData.Bytes(byteVector)
    }

    override fun processReverseTyped(data: ProcessingData.Bytes): ProcessingData.Opc2 {
        val reader = data.byteVector.getReader()
        val basesOpcBase = baseManager.reverse(reader)
        val basesOpc = opcManager.reverse(reader, basesOpcBase)
        val basesData = moduleBasesOpc2.processReverseTyped(ProcessingData.Opc2(basesOpc))
        val reverse = opcManager.reverse(reader, basesData.triple)
        return ProcessingData.Opc2(reverse)
    }
}