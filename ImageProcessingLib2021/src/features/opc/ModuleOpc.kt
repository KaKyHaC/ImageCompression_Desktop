package features.opc

import data_model.generics.Triple
import data_model.processing_data.ProcessingData
import features.AbsDataProcessor
import features.opc.managers.OpcProcessingUnit

class ModuleOpc : AbsDataProcessor<ProcessingData.Image, ProcessingData.OPC>(
        ProcessingData.Image::class, ProcessingData.OPC::class
) {

    private val opcProcessingUnit = OpcProcessingUnit()

    override fun processDirectTyped(data: ProcessingData.Image): ProcessingData.OPC {
        val direct1 = opcProcessingUnit.direct(data.triple.first)
        val direct2 = opcProcessingUnit.direct(data.triple.second)
        val direct3 = opcProcessingUnit.direct(data.triple.third)
        return ProcessingData.OPC(Triple(direct1, direct2, direct3))
    }

    override fun processReverseTyped(data: ProcessingData.OPC): ProcessingData.Image {
        val direct1 = opcProcessingUnit.reverse(data.triple.first)
        val direct2 = opcProcessingUnit.reverse(data.triple.second)
        val direct3 = opcProcessingUnit.reverse(data.triple.third)
        return ProcessingData.Image(Triple(direct1, direct2, direct3))
    }
}