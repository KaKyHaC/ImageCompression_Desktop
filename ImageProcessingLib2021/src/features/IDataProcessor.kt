package features

import data_model.processing_data.IProcessingData

interface IDataProcessor {
    fun process(data: IProcessingData): IProcessingData
}