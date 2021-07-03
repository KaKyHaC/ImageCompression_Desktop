package features

import data_model.processing_data.ProcessingData

abstract class AbsDataProcessor<IN : ProcessingData, OUT: ProcessingData>(
        private val inClass: Class<IN>,
        private val outClass: Class<OUT>
) : IDataProcessor {

    override fun processDirect(data: ProcessingData): ProcessingData {
        return if (data.javaClass == inClass) processDirectTyped(inClass.cast(data))
        else throw Exception("${this.javaClass} can't processDirect this data = $data")
    }

    override fun processReverse(data: ProcessingData): ProcessingData {
        return if (data.javaClass == outClass) processReverseTyped(outClass.cast(data))
        else throw Exception("${this.javaClass} can't processReverse this data = $data")
    }

    protected abstract fun processDirectTyped(data: IN): OUT

    protected abstract fun processReverseTyped(data: OUT): IN
}