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

    private fun <T : Any> calculateMatrixOfMatrixSize(matrix: Matrix<T>, horizontalStep: Int, verticalStep: Int): Size {
        var w = matrix.width / horizontalStep
        var h = matrix.height / verticalStep
        if (matrix.width % horizontalStep != 0) w++
        if (matrix.height % verticalStep != 0) h++
        return Size(w, h)
    }

    private fun isMatrixOfMatrixSafe(originSize: Size, childSize: Size) =
            originSize.width % childSize.width == 0 &&
                    originSize.height % childSize.height == 0

    fun <T : Any> splitIterator(matrix: Matrix<T>, childSize: Size, defaultValue: T? = null): Matrix<Matrix<T>> {
        val parentSize = calculateMatrixOfMatrixSize(matrix, childSize.width, childSize.width)
        val tmpDefValue = if (isMatrixOfMatrixSafe(matrix.size, childSize)) null else defaultValue
        return Matrix.create(parentSize) { i, j ->
            val wStart = i * childSize.width
            val hStart = j * childSize.height
            rectIterator(matrix, wStart, hStart, childSize, tmpDefValue)
        }
    }

    fun <T : Any> cropMatrix(origin: Matrix<T>, target: Size) =
            IteratorMatrix(origin, 0, 0, target)

    fun mulMat(mat0: Matrix<Float>, mat1: Matrix<Float>): Matrix<Float> {
        val ret = Array(mat0.width) { Array(mat1.width) { 0f } }
        for (i in 0 until mat0.width) {
            for (j in 0 until mat1.height) {
                for (k in 0 until mat1.height) {
                    ret[i][j] += mat0[i, k] * mat1[k, j]
                }
            }
        }
        return Matrix(ret, Float::class)
    }
}