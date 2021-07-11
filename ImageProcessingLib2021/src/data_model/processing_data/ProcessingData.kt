package data_model.processing_data

import data_model.generics.Triple
import data_model.generics.matrix.Matrix
import data_model.types.ByteVector
import data_model.types.DataOpc
import data_model.types.DataOpc2

sealed class ProcessingData {

    data class File(val file: java.io.File) : ProcessingData()

    data class Image(val triple: Triple<Matrix<Short>>) : ProcessingData()

    data class Opc(val triple: Triple<Matrix<out DataOpc>>) : ProcessingData()

    data class Opc2(val triple: Triple<Matrix<DataOpc2>>) : ProcessingData() {
        data class Bases(val triple: Triple<Matrix<DataOpc2.Base>>) : ProcessingData() {
            constructor(opc2: Opc2) : this(
                    opc2.triple.map { it.map { i, j, dataOpc2 -> dataOpc2.base } }
            )
        }
    }

    data class Bytes(val byteVector: ByteVector) : ProcessingData()
}