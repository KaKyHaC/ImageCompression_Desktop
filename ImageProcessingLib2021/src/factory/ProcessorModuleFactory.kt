package factory

import features.IDataProcessor

object ProcessorModuleFactory {
    fun getModule(params: ModuleParams): IDataProcessor {
        TODO()
    }

    sealed class ModuleParams
}