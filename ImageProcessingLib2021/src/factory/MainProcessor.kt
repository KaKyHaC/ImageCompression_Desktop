package factory

import data_model.processing_data.ProcessingData

class MainProcessor(val params: Params) {

    private val modules = params.moduleParams.map { ProcessorModuleFactory.getModule(it) }

    fun run(data: ProcessingData, mode: Mode = Mode.DIRECT): ProcessingData {
        var tmp = data
        when (mode) {
            Mode.DIRECT -> modules.forEach { tmp = it.processDirect(tmp) }
            Mode.REVERSE -> modules.reversed().forEach { tmp = it.processReverse(tmp) }
        }
        return tmp
    }

    enum class Mode { DIRECT, REVERSE }

    data class Params(val moduleParams: List<ProcessorModuleFactory.ModuleParams>)
}