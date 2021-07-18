package features.quantization

import data_model.generics.Triple
import data_model.processing_data.ProcessingData
import features.AbsDataProcessor
import features.quantization.manager.QuantizationManager
import features.quantization.manager.QuantizationUnit

class ModuleQuantization(
        val parameters: Parameters = Parameters()
) : AbsDataProcessor<ProcessingData.Image, ProcessingData.Image>(
        ProcessingData.Image::class, ProcessingData.Image::class
) {

    data class Parameters(
            val first: QuantizationManager.Parameters = QuantizationManager.Parameters(
                    QuantizationUnit.Parameters(tableType = QuantizationUnit.TableType.LUMINOSITY(true))
            ),
            val second: QuantizationManager.Parameters = QuantizationManager.Parameters(
                    QuantizationUnit.Parameters(tableType = QuantizationUnit.TableType.CHROMATICITY(true))
            ),
            val third: QuantizationManager.Parameters = QuantizationManager.Parameters(
                    QuantizationUnit.Parameters(tableType = QuantizationUnit.TableType.CHROMATICITY(true))
            )
    )

    private val manager1 = QuantizationManager(parameters.first)
    private val manager2 = QuantizationManager(parameters.second)
    private val manager3 = QuantizationManager(parameters.third)

    override fun processDirectTyped(data: ProcessingData.Image): ProcessingData.Image {
        val direct1 = manager1.direct(data.triple.first)
        val direct2 = manager2.direct(data.triple.second)
        val direct3 = manager3.direct(data.triple.third)
        return ProcessingData.Image(Triple(direct1, direct2, direct3))
    }

    override fun processReverseTyped(data: ProcessingData.Image): ProcessingData.Image {
        val direct1 = manager1.reverse(data.triple.first)
        val direct2 = manager2.reverse(data.triple.second)
        val direct3 = manager3.reverse(data.triple.third)
        return ProcessingData.Image(Triple(direct1, direct2, direct3))
    }
}