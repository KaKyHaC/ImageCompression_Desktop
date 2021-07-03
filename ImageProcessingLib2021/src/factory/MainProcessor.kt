package factory

import data_model.processing_data.ProcessingData

object MainProcessor {

    fun run(params: Params): Result {
        val modules = params.moduleParams.map { ProcessorModuleFactory.getModule(it) }
        var data = params.initData
        modules.forEach { data = it.processDirect(data) }
        return Result(data)
    }

    data class Params(
            val id: String = "",
            val initData: ProcessingData,
            val moduleParams: List<ProcessorModuleFactory.ModuleParams>
    )

    data class Result(
            val resultData: ProcessingData
    )
}