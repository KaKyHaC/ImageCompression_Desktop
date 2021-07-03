package factory

import data_model.processing_data.ProcessingData

object MainProcessor {

    fun runDirect(input: Input): Output {
        val modules = input.moduleParams.map { ProcessorModuleFactory.getModule(it) }
        var data = input.data
        modules.forEach { data = it.processDirect(data) }
        return Output(data, input.moduleParams)
    }

    fun runReverse(input: Output): Input {
        val modules = input.moduleParams.map { ProcessorModuleFactory.getModule(it) }.reversed()
        var data = input.data
        modules.forEach { data = it.processDirect(data) }
        return Input(data, input.moduleParams)
    }

    data class Input(
            val data: ProcessingData,
            val moduleParams: List<ProcessorModuleFactory.ModuleParams>
    )

    data class Output(
            val data: ProcessingData,
            val moduleParams: List<ProcessorModuleFactory.ModuleParams>
    )
}