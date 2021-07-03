package data_model.containers.matrix

import data_model.containers.types.DataOpc
import data_model.containers.types.Size

class DataOpcMatrix(matrix: Array<Array<DataOpc>>) : Matrix<DataOpc>(matrix) {

    constructor(
            size: Size, init: (Int, Int) -> DataOpc
    ) : this(Array(size.width) { i -> Array(size.height) { j -> init(i, j) } })
}