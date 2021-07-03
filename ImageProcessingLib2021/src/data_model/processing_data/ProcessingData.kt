package data_model.processing_data

import data_model.containers.Triple
import data_model.containers.matrix.Matrix
import data_model.types.DataOpc

sealed class ProcessingData {

    data class Image(val triple: Triple<Matrix<Short>>)
    data class OPC(val triple: Triple<Matrix<DataOpc>>)
}