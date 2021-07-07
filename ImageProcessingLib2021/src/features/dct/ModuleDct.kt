package features.dct

import data_model.generics.Triple
import data_model.processing_data.ProcessingData
import features.AbsDataProcessor
import features.dct.manager.DctManager

class ModuleDct(
        val parameters: Parameters = Parameters()
) : AbsDataProcessor<ProcessingData.Image, ProcessingData.Image>(
        ProcessingData.Image::class, ProcessingData.Image::class
) {

    data class Parameters(
            val processFirst: DctManager.Parameters? = DctManager.Parameters(),
            val processSecond: DctManager.Parameters? = DctManager.Parameters(),
            val processThird: DctManager.Parameters? = DctManager.Parameters()
    )

    private val manager1 = parameters.processFirst?.let { DctManager(it) }
    private val manager2 = parameters.processSecond?.let { DctManager(it) }
    private val manager3 = parameters.processThird?.let { DctManager(it) }

    override fun processDirectTyped(data: ProcessingData.Image): ProcessingData.Image {
        return ProcessingData.Image(
                Triple(
                        manager1?.direct(data.triple.first) ?: data.triple.first,
                        manager2?.direct(data.triple.second) ?: data.triple.second,
                        manager3?.direct(data.triple.third) ?: data.triple.third
                )
        )
    }

    override fun processReverseTyped(data: ProcessingData.Image): ProcessingData.Image {
        return ProcessingData.Image(
                Triple(
                        manager1?.reverse(data.triple.first) ?: data.triple.first,
                        manager2?.reverse(data.triple.second) ?: data.triple.second,
                        manager3?.reverse(data.triple.third) ?: data.triple.third
                )
        )
    }
}