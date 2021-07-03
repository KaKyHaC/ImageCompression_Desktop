package data_model.processing_data

import data_model.generics.Triple
import data_model.generics.matrix.Matrix
import data_model.types.ByteVector
import data_model.types.DataOpc

sealed class ProcessingData {

    data class File(val file: File) : ProcessingData()

    sealed class Image(val triple: Triple<Matrix<Short>>) : ProcessingData() {
        class Jpeg(triple: Triple<Matrix<Short>>) : Image(triple)
        class YCbCr(triple: Triple<Matrix<Short>>) : Image(triple)
    }

    data class OPC(val triple: Triple<Matrix<DataOpc>>) : ProcessingData()

    data class Bytes(val byteVector: ByteVector): ProcessingData()
}