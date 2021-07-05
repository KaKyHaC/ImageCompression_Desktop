package features.enlargement

import data_model.generics.Triple
import data_model.processing_data.ProcessingData
import features.AbsDataProcessor
import features.enlargement.utils.EnlargementUtils

class ModuleEnlargement(
        val processFirst: Boolean = false,
        val processSecond: Boolean = true,
        val processThird: Boolean = true
) : AbsDataProcessor<ProcessingData.Image, ProcessingData.Image>(
        ProcessingData.Image::class, ProcessingData.Image::class
) {

    override fun processDirectTyped(data: ProcessingData.Image): ProcessingData.Image {
        return ProcessingData.Image(
                Triple(
                        if (processFirst) EnlargementUtils.direct(data.triple.first) else data.triple.first,
                        if (processSecond) EnlargementUtils.direct(data.triple.second) else data.triple.second,
                        if (processThird) EnlargementUtils.direct(data.triple.third) else data.triple.third
                )
        )
    }

    override fun processReverseTyped(data: ProcessingData.Image): ProcessingData.Image {
        return ProcessingData.Image(
                Triple(
                        if (processFirst) EnlargementUtils.reverse(data.triple.first) else data.triple.first,
                        if (processSecond) EnlargementUtils.reverse(data.triple.second) else data.triple.second,
                        if (processThird) EnlargementUtils.reverse(data.triple.third) else data.triple.third
                )
        )
    }
}