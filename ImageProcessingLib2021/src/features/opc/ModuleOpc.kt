package features.opc

import data_model.generics.Triple
import data_model.processing_data.ProcessingData
import features.AbsDataProcessor
import features.opc.managers.OpcProcessingUnit

class ModuleOpc(
        val parameters: Parameters = Parameters()
) : AbsDataProcessor<ProcessingData.Image, ProcessingData.OPC>(
        ProcessingData.Image::class, ProcessingData.OPC::class
) {
    data class Parameters(
            val first: OpcProcessingUnit.Parameters = OpcProcessingUnit.Parameters(),
            val second: OpcProcessingUnit.Parameters = OpcProcessingUnit.Parameters(),
            val third: OpcProcessingUnit.Parameters = OpcProcessingUnit.Parameters()
    )

    private val opcProcessingUnit1 = OpcProcessingUnit(parameters.first)
    private val opcProcessingUnit2 = OpcProcessingUnit(parameters.second)
    private val opcProcessingUnit3 = OpcProcessingUnit(parameters.third)

    override fun processDirectTyped(data: ProcessingData.Image): ProcessingData.OPC {
        val direct1 = opcProcessingUnit1.direct(data.triple.first)
        val direct2 = opcProcessingUnit2.direct(data.triple.second)
        val direct3 = opcProcessingUnit3.direct(data.triple.third)
        return ProcessingData.OPC(Triple(direct1, direct2, direct3))
    }

    override fun processReverseTyped(data: ProcessingData.OPC): ProcessingData.Image {
        val direct1 = opcProcessingUnit1.reverse(data.triple.first)
        val direct2 = opcProcessingUnit2.reverse(data.triple.second)
        val direct3 = opcProcessingUnit3.reverse(data.triple.third)
        return ProcessingData.Image(Triple(direct1, direct2, direct3))
    }
}