package utils

import data_model.containers.matrix.Matrix
import data_model.types.DataOpc
import data_model.types.Size

object MatrixFactory {

    fun createShortMatrix(
            size: Size, init: (Int, Int) -> Short = { _, _ -> 0 }
    ) = Matrix(Array(size.width) { i -> Array(size.height) { j -> init(i, j) } }, Short::class.java)

    fun createDataOpcMatrix(
            size: Size, init: (Int, Int) -> DataOpc
    ) = Matrix(Array(size.width) { i -> Array(size.height) { j -> init(i, j) } }, DataOpc::class.java)
}