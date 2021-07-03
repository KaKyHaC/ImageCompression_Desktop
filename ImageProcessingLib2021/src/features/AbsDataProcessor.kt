package features

import data_model.processing_data.ProcessingData
import kotlin.reflect.KClass
import kotlin.reflect.full.cast
import kotlin.reflect.full.isSubclassOf

abstract class AbsDataProcessor<IN : ProcessingData, OUT : ProcessingData>(
        private val inClass: KClass<IN>,
        private val outClass: KClass<OUT>
) : IDataProcessor {

    override fun processDirect(data: ProcessingData): ProcessingData {
        return if (data::class.isSubclassOf(inClass)) processDirectTyped(inClass.cast(data))
        else throw Exception("${this.javaClass} can't processDirect this data = $data")
    }

    override fun processReverse(data: ProcessingData): ProcessingData {
        return if (data::class.isSubclassOf(outClass)) processReverseTyped(outClass.cast(data))
        else throw Exception("${this.javaClass} can't processReverse this data = $data")
    }

    protected abstract fun processDirectTyped(data: IN): OUT

    protected abstract fun processReverseTyped(data: OUT): IN
}