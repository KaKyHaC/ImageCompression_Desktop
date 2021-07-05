package features

import data_model.processing_data.ProcessingData

interface IDataProcessor {
    fun processDirect(data: ProcessingData): ProcessingData
    fun processReverse(data: ProcessingData): ProcessingData
}