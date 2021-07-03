package utils

import data_model.containers.matrix.Matrix
import data_model.containers.types.DataOpc
import data_model.containers.types.Size

object MatrixFactory {

    fun createShortMatrix(
            size: Size, init: (Int, Int) -> Short = { _, _ -> 0 }
    ) = Matrix(Array(size.width) { i -> Array(size.height) { j -> init(i, j) } })

    fun createDataOpcMatrix(
            size: Size, init: (Int, Int) -> DataOpc
    ) = Matrix(Array(size.width) { i -> Array(size.height) { j -> init(i, j) } })
}