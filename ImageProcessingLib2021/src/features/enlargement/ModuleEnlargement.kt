package features.enlargement

import data_model.generics.Triple
import data_model.processing_data.ProcessingData
import features.AbsDataProcessor
import features.enlargement.utils.EnlargementUtils

class ModuleEnlargement(
        val parameters: Parameters = Parameters()
) : AbsDataProcessor<ProcessingData.Image, ProcessingData.Image>(
        ProcessingData.Image::class, ProcessingData.Image::class
) {

    data class Parameters(
            val processFirst: Boolean = false,
            val processSecond: Boolean = true,
            val processThird: Boolean = true
    )

    override fun processDirectTyped(data: ProcessingData.Image): ProcessingData.Image {
        return ProcessingData.Image(
                Triple(
                        if (parameters.processFirst) EnlargementUtils.direct(data.triple.first) else data.triple.first,
                        if (parameters.processSecond) EnlargementUtils.direct(data.triple.second) else data.triple.second,
                        if (parameters.processThird) EnlargementUtils.direct(data.triple.third) else data.triple.third
                )
        )
    }

    override fun processReverseTyped(data: ProcessingData.Image): ProcessingData.Image {
        return ProcessingData.Image(
                Triple(
                        if (parameters.processFirst) EnlargementUtils.reverse(data.triple.first) else data.triple.first,
                        if (parameters.processSecond) EnlargementUtils.reverse(data.triple.second) else data.triple.second,
                        if (parameters.processThird) EnlargementUtils.reverse(data.triple.third) else data.triple.third
                )
        )
    }
}