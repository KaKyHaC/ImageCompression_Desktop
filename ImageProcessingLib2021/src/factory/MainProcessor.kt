package factory

import data_model.processing_data.ProcessingData

object MainProcessor {

    fun run(params: Params, mode: Mode = Mode.DIRECT): Params {
        var modules = params.moduleParams.map { ProcessorModuleFactory.getModule(it) }
        if (mode == Mode.REVERSE) modules = modules.reversed()
        var data = params.data
        modules.forEach { data = it.processDirect(data) }
        return Params(data, params.moduleParams)
    }

    enum class Mode { DIRECT, REVERSE }

    data class Params(
            val data: ProcessingData,
            val moduleParams: List<ProcessorModuleFactory.ModuleParams>
    )
}