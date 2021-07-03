package features

import data_model.processing_data.ProcessingData

interface IDataProcessor {
    fun process(data: ProcessingData): ProcessingData
}