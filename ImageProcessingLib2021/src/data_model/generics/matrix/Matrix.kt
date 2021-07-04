package data_model.generics.matrix

import data_model.types.Size
import java.util.*
import kotlin.reflect.KClass

open class Matrix<T : Any>(
        val matrix: Array<Array<T>>,
        val type: KClass<T>
) : IMatrix<T> {

    override val width = matrix.size

    override val height = matrix.firstOrNull()?.size ?: 0

    val size get() = Size(width, height)

    override operator fun get(i: Int, j: Int) = matrix[i][j]

    override operator fun set(i: Int, j: Int, value: T) {
        matrix[i][j] = value
    }

    fun applyEach(invoke: (i: Int, j: Int, value: T) -> T?) {
        for (i in 0 until width) {
            for (j in 0 until height) {
                set(i, j, invoke.invoke(i, j, get(i, j)) ?: get(i, j))
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Matrix<T>

        for (i in 0 until width)
            for (j in 0 until height)
                if (matrix[i][j] != other[i, j])
                    return false

        return true
    }

    override fun hashCode(): Int {
        return Arrays.hashCode(matrix)
    }

    override fun toString(): String {
        val sb = StringBuilder()
        for (j in 0 until height) {
            for (i in 0 until width) {
                sb.append("${(get(i, j))},")
            }
            sb.append("\n")
        }
        return sb.toString()
    }

    companion object {
        inline fun <reified T : Any> create(
                size: Size, init: (Int, Int) -> T
        ) = Matrix(Array(size.width) { i -> Array(size.height) { j -> init(i, j) } }, T::class)
    }
}