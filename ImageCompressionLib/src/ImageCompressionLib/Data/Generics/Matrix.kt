package ImageCompressionLib.Data.Generics

import ImageCompressionLib.Data.Interfaces.ICopyable
import ImageCompressionLib.Data.Interfaces.IMatrix
import ImageCompressionLib.Data.Primitives.Size

class Matrix<T>(
    private val matrix: MutableList<MutableList<T>>
) : IMatrix<T>, ICopyable<Matrix<T>> {

    constructor(size: Size, init: (i: Int, j: Int) -> T) : this(
        MutableList(size.width) { i -> MutableList(size.height) { j -> init(i, j) } }
    )

    override fun get(i: Int, j: Int) = matrix.getOrNull(i)?.getOrNull(j)

    override fun set(i: Int, j: Int, value: T) {
        matrix[i][j] = value
    }

    override fun forEachIndexed(invoke: (i: Int, j: Int, value: T) -> Unit) {
        matrix.forEachIndexed { i, array -> array.forEachIndexed { j, value -> invoke(i, j, value) } }
    }

    override fun <R> mapIndexed(invoke: (i: Int, j: Int, value: T) -> R) = Matrix(
        MutableList(width) { i -> MutableList(height) { j -> invoke(i, j, get(i,j)!!) } }
    )

    override fun copy() = mapIndexed {i, j, value -> if (value is ICopyable<*>) value.copy() as T else value }

    override val width: Int
        get() = matrix.size
    override val height: Int
        get() = matrix.firstOrNull()?.size ?: 0
}