package features

import data_model.processing_data.ProcessingData

abstract class AbsDataProcessor<T : ProcessingData>(
        private val dataClass: Class<T>
) : IDataProcessor {

    override fun process(data: ProcessingData): ProcessingData {
        return if (data.javaClass == dataClass) processTyped(dataClass.cast(data))
        else throw Exception("${this.javaClass} can't process this data = $data")
    }

    protected abstract fun processTyped(data: T): ProcessingData
}