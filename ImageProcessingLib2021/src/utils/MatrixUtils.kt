package utils

import data_model.generics.matrix.IteratorDefaultMatrix
import data_model.generics.matrix.IteratorMatrix
import data_model.generics.matrix.Matrix
import data_model.types.DataOpc
import data_model.types.Size

object MatrixUtils {

    @Deprecated("use create")
    fun createShortMatrix(
            size: Size, init: (Int, Int) -> Short = { _, _ -> 0 }
    ) = Matrix.create(size) { i, j -> init(i, j) }

    @Deprecated("use create")
    fun createDataOpcMatrix(
            size: Size, init: (Int, Int) -> DataOpc
    ) = Matrix.create(size) { i, j -> init(i, j) }

    inline fun <reified T : Any> rectBuffer(
            matrix: Matrix<T>,
            wStart: Int,
            hStart: Int,
            size: Size
    ): Matrix<T> {
        return Matrix.create(size) { i, j -> matrix[i + wStart, j + hStart] }
    }

    fun <T : Any> rectIterator(
            matrix: Matrix<T>,
            wStart: Int,
            hStart: Int,
            size: Size,
            defaultValue: T? = null
    ): Matrix<T> {
        return defaultValue
                ?.let { IteratorDefaultMatrix(matrix, wStart, hStart, size, it) }
                ?: IteratorMatrix(matrix, wStart, hStart, size)
    }

}