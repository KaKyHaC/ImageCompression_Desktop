package data_model.generics

import java.lang.IndexOutOfBoundsException

data class Triple<T>(
        val first: T,
        val second: T,
        val third: T
) : Iterable<T> {

    inline fun <reified R> map(mapper: (T) -> R) = Triple(
            mapper(first),
            mapper(second),
            mapper(third)
    )

    override fun iterator() = listOf(first, second, third).iterator()

    operator fun get(index: Int) = when (index) {
        0 -> first
        1 -> second
        2 -> third
        else -> throw IndexOutOfBoundsException(index)
    }
}