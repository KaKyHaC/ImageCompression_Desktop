package features.opc2

import data_model.generics.Triple
import data_model.processing_data.ProcessingData
import features.AbsDataProcessor
import features.opc2.managers.OpcProcessingManager2

class ModuleOpc2(
        val parameters: Parameters = Parameters()
) : AbsDataProcessor<ProcessingData.Image, ProcessingData.Opc2>(
        ProcessingData.Image::class, ProcessingData.Opc2::class
) {
    data class Parameters(
            val first: OpcProcessingManager2.Parameters = OpcProcessingManager2.Parameters(),
            val second: OpcProcessingManager2.Parameters = OpcProcessingManager2.Parameters(),
            val third: OpcProcessingManager2.Parameters = OpcProcessingManager2.Parameters()
    )

    private val opcProcessingUnit1 = OpcProcessingManager2(parameters.first)
    private val opcProcessingUnit2 = OpcProcessingManager2(parameters.second)
    private val opcProcessingUnit3 = OpcProcessingManager2(parameters.third)

    override fun processDirectTyped(data: ProcessingData.Image): ProcessingData.Opc2 {
        val direct1 = opcProcessingUnit1.direct(data.triple.first)
        val direct2 = opcProcessingUnit2.direct(data.triple.second)
        val direct3 = opcProcessingUnit3.direct(data.triple.third)
        val size1 = data.triple.first.size
        val size2 = data.triple.second.size
        val size3 = data.triple.third.size
        return ProcessingData.Opc2(Triple(direct1, direct2, direct3), Triple(size1, size2, size3))
    }

    override fun processReverseTyped(data: ProcessingData.Opc2): ProcessingData.Image {
        val direct1 = opcProcessingUnit1.reverse(data.triple.first, data.originSize?.first)
        val direct2 = opcProcessingUnit2.reverse(data.triple.second, data.originSize?.second)
        val direct3 = opcProcessingUnit3.reverse(data.triple.third, data.originSize?.third)
        return ProcessingData.Image(Triple(direct1, direct2, direct3))
    }
}