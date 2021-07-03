package features

import data_model.IData

interface IDataProcessor {
    fun process(data: IData): IData
}