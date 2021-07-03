package features

import data_model.IData

abstract class AbsDataProcessor<T : IData>(
        private val dataClass: Class<T>
) : IDataProcessor {

    override fun process(data: IData): IData {
        return if (data.javaClass == dataClass) processTyped(dataClass.cast(data))
        else throw Exception("${this.javaClass} can't process this data = $data")
    }

    protected abstract fun processTyped(data: T): IData
}