package features.dct

import data_model.generics.Triple
import data_model.processing_data.ProcessingData
import features.AbsDataProcessor
import features.dct.manager.DctUnit
import features.enlargement.utils.EnlargementUtils

class ModuleDct(
        val parameters: Parameters
) : AbsDataProcessor<ProcessingData.Image, ProcessingData.Image>(
        ProcessingData.Image::class, ProcessingData.Image::class
) {

    data class Parameters(
            val processFirst: DctUnit.Parameters? = DctUnit.Parameters(),
            val processSecond: DctUnit.Parameters? = DctUnit.Parameters(),
            val processThird: DctUnit.Parameters? = DctUnit.Parameters()
    )

    private val dctUnit1 = parameters.processFirst?.let { DctUnit(it) }
    private val dctUnit2 = parameters.processSecond?.let { DctUnit(it) }
    private val dctUnit3 = parameters.processThird?.let { DctUnit(it) }

    override fun processDirectTyped(data: ProcessingData.Image): ProcessingData.Image {
        return ProcessingData.Image(
                Triple(
                        dctUnit1?.direct(data.triple.first) ?: data.triple.first,
                        dctUnit2?.direct(data.triple.second) ?: data.triple.second,
                        dctUnit3?.direct(data.triple.third) ?: data.triple.third
                )
        )
    }

    override fun processReverseTyped(data: ProcessingData.Image): ProcessingData.Image {
        return ProcessingData.Image(
                Triple(
                        dctUnit1?.reverse(data.triple.first) ?: data.triple.first,
                        dctUnit2?.reverse(data.triple.second) ?: data.triple.second,
                        dctUnit3?.reverse(data.triple.third) ?: data.triple.third
                )
        )
    }
}