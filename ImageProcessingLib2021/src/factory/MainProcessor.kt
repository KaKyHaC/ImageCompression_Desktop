package factory

import data_model.processing_data.ProcessingData

object MainProcessor {

    fun run(params: Params, mode: Mode = Mode.DIRECT): Params {
        val modules = params.moduleParams.map { ProcessorModuleFactory.getModule(it) }
        var data = params.data
        when (mode) {
            Mode.DIRECT -> modules.forEach { data = it.processDirect(data) }
            Mode.REVERSE -> modules.reversed().forEach { data = it.processReverse(data) }
        }
        return Params(data, params.moduleParams)
    }

    enum class Mode { DIRECT, REVERSE }

    data class Params(
            val data: ProcessingData,
            val moduleParams: List<ProcessorModuleFactory.ModuleParams>
    )
}