package features.opc.utils

import data_model.generics.matrix.Matrix
import data_model.types.DataOpc

class OpcProcessingUnit {

    fun direct(image: Matrix<Short>): Matrix<DataOpc> {
        TODO()
    }

    fun reverse(dataOpcMatrix: Matrix<DataOpc>): Matrix<Short> {
        TODO()
    }
}