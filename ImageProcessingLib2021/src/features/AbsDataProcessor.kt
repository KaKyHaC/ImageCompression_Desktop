package features

import data_model.processing_data.IProcessingData

abstract class AbsDataProcessor<T : IProcessingData>(
        private val dataClass: Class<T>
) : IDataProcessor {

    override fun process(data: IProcessingData): IProcessingData {
        return if (data.javaClass == dataClass) processTyped(dataClass.cast(data))
        else throw Exception("${this.javaClass} can't process this data = $data")
    }

    protected abstract fun processTyped(data: T): IProcessingData
}