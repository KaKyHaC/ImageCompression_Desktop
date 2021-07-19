package factory

import data_model.processing_data.ProcessingData
import utils.TimeCalculatorUtils

class MainProcessor(val params: Params) {

    private val modules = params.moduleParams.map { ProcessorModuleFactory.getModule(it) }

    fun run(data: ProcessingData, mode: Mode = Mode.DIRECT): ProcessingData {
        TimeCalculatorUtils.startNewTrack("start $mode: $data")
        var tmp = data
        when (mode) {
            Mode.DIRECT -> modules.forEach { tmp = it.processDirect(tmp); TimeCalculatorUtils.append(it.toString()) }
            Mode.REVERSE -> modules.reversed().forEach { tmp = it.processReverse(tmp); TimeCalculatorUtils.append(it.toString()) }
        }
        println("TimeCalculatorUtils = " + TimeCalculatorUtils.getInfoInPercent())
        return tmp
    }

    enum class Mode { DIRECT, REVERSE }

    data class Params(val moduleParams: List<ProcessorModuleFactory.ModuleParams>)
}