package features.opc2

import data_model.generics.Triple
import data_model.processing_data.ProcessingData
import features.AbsDataProcessor
import features.opc2.managers.OpcBasesProcessingManager2

class ModuleBasesOpc2(
        val parameters: Parameters = Parameters()
) : AbsDataProcessor<ProcessingData.Opc2.Bases, ProcessingData.Opc2>(
        ProcessingData.Opc2.Bases::class, ProcessingData.Opc2::class
) {
    data class Parameters(
            val first: OpcBasesProcessingManager2.Parameters = OpcBasesProcessingManager2.Parameters(),
            val second: OpcBasesProcessingManager2.Parameters = OpcBasesProcessingManager2.Parameters(),
            val third: OpcBasesProcessingManager2.Parameters = OpcBasesProcessingManager2.Parameters()
    )

    private val opcProcessingUnit1 = OpcBasesProcessingManager2(parameters.first)
    private val opcProcessingUnit2 = OpcBasesProcessingManager2(parameters.second)
    private val opcProcessingUnit3 = OpcBasesProcessingManager2(parameters.third)

    override fun processDirectTyped(data: ProcessingData.Opc2.Bases): ProcessingData.Opc2 {
        val res1 = opcProcessingUnit1.direct(data.triple.first)
        val res2 = opcProcessingUnit2.direct(data.triple.second)
        val res3 = opcProcessingUnit3.direct(data.triple.third)
        return ProcessingData.Opc2(Triple(res1, res2, res3))
    }

    override fun processReverseTyped(data: ProcessingData.Opc2): ProcessingData.Opc2.Bases {
        val res1 = opcProcessingUnit1.reverse(data.triple.first)
        val res2 = opcProcessingUnit2.reverse(data.triple.second)
        val res3 = opcProcessingUnit3.reverse(data.triple.third)
        return ProcessingData.Opc2.Bases(Triple(res1, res2, res3))
    }
}