package features.opc_format

import data_model.processing_data.ProcessingData
import data_model.types.ByteVector
import data_model.types.DataOpc2
import features.AbsDataProcessor
import features.opc_format.manager.OpcToBytesManager

class ModuleOpcWithoutBaseToBytes(
        val parameters: Parameters = Parameters()
) : AbsDataProcessor<ProcessingData.Opc2, ProcessingData.BytesAndBases>(
        ProcessingData.Opc2::class, ProcessingData.BytesAndBases::class
) {
    class Parameters(
            val opcParams: OpcToBytesManager.Parameters = OpcToBytesManager.Parameters()
    )

    private val opcManager = parameters.opcParams.let { OpcToBytesManager(it) }

    override fun processDirectTyped(data: ProcessingData.Opc2): ProcessingData.BytesAndBases {
        val byteVector = ByteVector()
        val len1 = byteVector.getBytes().size
        opcManager.direct(byteVector, data.triple)
        val len2 = byteVector.getBytes().size - len1
        println("len1 = ${len1}, len2 = $len2")
        val tripleBases = data.triple.map { it.map { i: Int, j: Int, value: DataOpc2 -> value.base } }
        return ProcessingData.BytesAndBases(byteVector, tripleBases)
    }

    override fun processReverseTyped(data: ProcessingData.BytesAndBases): ProcessingData.Opc2 {
        val reader = data.byteVector.getReader()
        val opcs = opcManager.reverse(reader, data.tripleBases)
        return ProcessingData.Opc2(opcs)
    }
}